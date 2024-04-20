package tukano.impl.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A class interface to perform service discovery based on periodic
 * announcements over multicast communication.
 */
public interface Discovery {

	/**
	 * Get the instance of the Discovery service
	 * 
	 * @return the singleton instance of the Discovery service
	 */
	public static Discovery getInstance() {
		return DiscoveryImpl.getInstance();
	}

	/**
	 * Used to announce the URI of the given service name.
	 * 
	 * @param serviceName - the name of the service
	 * @param serviceURI  - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI);

	/**
	 * Get discovered URIs for a given service name
	 * 
	 * @param serviceName - name of the service
	 * @param minReplies  - minimum number of requested URIs. Blocks until the
	 *                    number is satisfied.
	 * @return array with the discovered URIs for the given service name.
	 */
	public URI[] knownUrisOf(String serviceName, int minReplies);

	/**
	 * Method to return the URI TTL for detection of servers that might have gonne
	 * out of service
	 * 
	 * @param uri
	 * @return
	 */
	public Long getURIAnnounceTime(URI uri);

	/**
	 * Remove URI with old TTL from server that might have gonne out of service
	 * 
	 * @param serviceName
	 * @param uri
	 */
	public void removeOldURI(String serviceName, URI uri);

}

/**
 * Implementation of the multicast discovery service
 */
class DiscoveryImpl implements Discovery {
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	// The pre-aggreed multicast endpoint assigned to perform discovery.
	private static final String DISCOVERY_MULTICAST_IP = "239.1.1.1";
	private static final int DISCOVERY_MULTICAST_PORT = 52262;

	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress(DISCOVERY_MULTICAST_IP,
			DISCOVERY_MULTICAST_PORT);

	// Delimiter to separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	// discovery config values
	static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;
	static final int DISCOVERY_RETRY_TIMEOUT = 5000;
	private static final int MAX_TRIES_TO_GET_URIS = 10;
	private static final int MAX_DATAGRAM_SIZE = 65536;

	/**
	 * Discovery instance to make it a singleton
	 */
	private static Discovery singleton;

	/**
	 * ConcurrentHashMap to store the Service Name and a Set of URIs of servers of
	 * that service (Set so we won't repeat the same URI; HashSet is not thread safe
	 * per se but it's handled by the outer ConcurrentHashMap
	 */
	private Map<String, Set<URI>> discoveredURIs;

	/**
	 * ConcurrentHashMap to store the time of last announcment of each URI, which
	 * might be usefull to discard servers who don't announce themselves anymore
	 */
	private Map<URI, Long> URI_TTL;

	/**
	 * Method to create a singleton DiscoveryImpl, syncronized for thread safety
	 */
	synchronized static Discovery getInstance() {
		if (singleton == null) {
			singleton = new DiscoveryImpl();
		}
		return singleton;
	}

	/**
	 * private constructor to be called by getInstance()
	 */
	private DiscoveryImpl() {
		discoveredURIs = new ConcurrentHashMap<>();
		URI_TTL = new ConcurrentHashMap<>();
		this.startListener();
	}

	/**
	 * Start Listening for
	 */
	private void startListener() {
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d", DISCOVERY_ADDR.getAddress(),
				DISCOVERY_ADDR.getPort()));

		new Thread(() -> {
			try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
				ms.joinGroup(DISCOVERY_ADDR, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
				while (true) {
					try {
						var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
						ms.receive(pkt);

						var msg = new String(pkt.getData(), 0, pkt.getLength());

						var parts = msg.split(DELIMITER);
						if (parts.length == 2) {
							var serviceName = parts[0];
							var uri = URI.create(parts[1]);
							// add URI to the corresponding service
							discoveredURIs.computeIfAbsent(serviceName, key -> new HashSet<>()).add(uri);
							// update uri TTL
							URI_TTL.put(uri, System.currentTimeMillis());
						}
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}).start();
	}

	@Override
	public void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName,
				serviceURI));

		var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
		var pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);

		// start thread to send periodic announcements
		new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				while (true) {
					try {
						ds.send(pkt);
						Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public URI[] knownUrisOf(String serviceName, int minEntries) {
		Log.info(String.format("Fetching current known URIs for service '%s' - waiting for %d minimum entries",
				serviceName, minEntries));

		Set<URI> svc_uris;
		int attempts = 0;

		while ((svc_uris = discoveredURIs.getOrDefault(serviceName, new HashSet<>())).size() < minEntries) {
			try {
				Thread.sleep(DISCOVERY_RETRY_TIMEOUT);
				attempts++;
				if (attempts >= MAX_TRIES_TO_GET_URIS) {
					Log.info("knownUrisOf: Exceeded maximum attempts to wait for minEntries of URIs for service "
							+ serviceName);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.info("Returned the array of URIs for service " + serviceName);
		return svc_uris.toArray(new URI[minEntries]);
	}

	@Override
	public Long getURIAnnounceTime(URI uri) {
		return URI_TTL.getOrDefault(uri, Long.MIN_VALUE);
	}

	@Override
	public void removeOldURI(String serviceName, URI uri) {
		discoveredURIs.computeIfPresent(serviceName, (svc, uris_set) -> {
			uris_set.remove(uri);
			return uris_set;
		});
		URI_TTL.remove(uri);
		Log.info(String.format("URI %s of service %s was removed.", uri, serviceName));
	}

}
package tukano.impl.blobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.clients.client_factory.ShortsClientFactory;

public class JavaBlobs implements Blobs{

    private static Logger LOG = Logger.getLogger(JavaBlobs.class.getName());

	@Override
	public Result<Void> upload(String blobId, byte[] bytes) {
		LOG.info("Uploading Blob with blobId: " + blobId);

		// Check validity of arguments
		if (blobId == null || bytes == null) {
			LOG.info("Invalid parameters");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}

		//Check shortId validity
		Shorts shortsClient = new ShortsClientFactory().getClient();
		if (shortsClient == null)
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		Result<Void> result = shortsClient.checkBlobId(blobId);
		if (!result.isOK()) {
			LOG.info("Wrong shortId");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}


		try {
            
			//Check if file exists
			Path path = Paths.get(blobId);
			if (Files.exists(path)) {
				byte[] existingBytes = Files.readAllBytes(path);
				if (Arrays.equals(existingBytes, bytes)) {
					LOG.info("Blob " + blobId + " already exists with matching bytes.");
					return Result.ok();
				} else {
					LOG.info("CONFLICT: Blob " + blobId + " already exists with NOT matching bytes.");
					return Result.error(Result.ErrorCode.CONFLICT);
				}
			}

			//Save file
			Files.write(path, bytes);
			LOG.info("Blob " + blobId + " saved.");

			return Result.ok();
		} catch (IOException e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

	@Override
	public Result<byte[]> download(String blobId) {
		LOG.info("Download Blob with blobId: " + blobId);

		//Check validity of arguments
		if (blobId == null) {
			LOG.info("Invalid parameters");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}

		//If the file exists, return it
		try {
			Path path = Paths.get(blobId);
			if (Files.exists(path)) {
				byte[] bytes = Files.readAllBytes(path);
				LOG.info("Blob " + blobId + " downloaded.");
				return Result.ok(bytes);
			}
			LOG.info("Blob with " + blobId + " not found.");
			return Result.error(Result.ErrorCode.NOT_FOUND);
		} catch (IOException e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}


    
}

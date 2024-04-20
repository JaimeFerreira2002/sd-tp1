package tukano.impl.blobs;

import tukano.api.rest.RestBlobs;
import tukano.impl.rest.RESTResource;

public class RestBlobsResource extends RESTResource implements RestBlobs{

    protected final JavaBlobs impl;

    public RestBlobsResource(){
        this.impl = new JavaBlobs();
    }

    @Override
    public void upload(String blobId, byte[] bytes) {
         resultOrThrow(impl.upload(blobId, bytes));
    }

    @Override
    public byte[] download(String blobId) {
        return resultOrThrow(impl.download(blobId));
    }
    
}

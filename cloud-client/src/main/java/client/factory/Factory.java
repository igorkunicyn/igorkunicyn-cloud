package client.factory;

import client.service.impl.NettyNetworkService;
import client.service.impl.WorkWithFiles;

public class Factory {

    public static NettyNetworkService getNetworkService() {
        return NettyNetworkService.getInstance();
    }

    public static WorkWithFiles getWithFileWorkable() {
        return WorkWithFiles.getInstance();
    }
}

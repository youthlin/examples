package com.youthlin.example.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * @author youthlin.chen
 * @date 2019-06-09 12:00
 */
@Slf4j
public class ZkTest {
    private static class Master {
        private static final String MASTER_PATH = "/master";
        ZooKeeper zk;
        String serverId = Integer.toHexString(new Random(System.currentTimeMillis()).nextInt());
        boolean isLeader = false;

        Master() {
            try {
                zk = new ZooKeeper("127.0.0.1:2181", 15000, event -> log.info("WatchedEvent: {}", event));
            } catch (IOException e) {
                log.error("error create zk instance.", e);
                System.exit(-1);
            }
        }

        boolean hasMaster() {
            while (true) {
                try {
                    Stat stat = new Stat();
                    byte[] data = zk.getData(MASTER_PATH, false, stat);
                    log.info("leader id: {}", data);
                    isLeader = new String(data).intern().equals(serverId);
                    return true;
                } catch (KeeperException.NoNodeException e) {
                    log.warn("Exception:{}", e.getClass());
                    return false;
                } catch (Exception e) {
                    log.warn("Exception:{}", e.getClass());
                }
            }
        }

        void setMaster() {
            while (true) {
                try {
                    String result = zk.create(MASTER_PATH, serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    log.info("create result: {}", result);
                    isLeader = true;
                    break;
                } catch (KeeperException.NodeExistsException e) {
                    isLeader = false;
                    log.warn("Exception:{}", e.getClass());
                    break;
                } catch (Exception e) {
                    log.warn("Exception:{}", e.getClass());
                }
                if (hasMaster()) {
                    break;
                }
            }
        }

        void setMasterAsync() {
            log.info("setMasterAsync...");
            zk.create(MASTER_PATH, serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, (rc, path, ctx, name) -> {
                KeeperException.Code code = KeeperException.Code.get(rc);
                log.info("code={}", code);
                switch (code) {
                    case OK:
                        isLeader = true;
                        break;
                    case CONNECTIONLOSS:
                        checkMasterAsync();
                        return;
                    default:
                        isLeader = false;
                }
                System.out.println("I'm " + (isLeader ? "" : "not ") + "the leader.");
            }, null);
        }

        void checkMasterAsync() {
            log.info("checkMasterAsync...");
            zk.getData(MASTER_PATH, false, (rc, path, ctx, data, stat) -> {
                KeeperException.Code code = KeeperException.Code.get(rc);
                log.info("code={}", code);
                switch (code) {
                    case CONNECTIONLOSS:
                        checkMasterAsync();
                        return;
                    case NONODE:
                        setMasterAsync();
                        return;
                    default:
                }
            }, null);
        }

    }

    public static void main(String[] args) throws Exception {
        Master m = new Master();
        m.setMasterAsync();
        Thread.sleep(60000);
        m.zk.close();
    }

}

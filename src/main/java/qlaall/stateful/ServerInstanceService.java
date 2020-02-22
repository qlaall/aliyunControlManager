package qlaall.stateful;

import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import qlaall.service.EcsAgentService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ServerInstanceService {
    @Autowired
    EcsAgentService ecsAgentService;
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    ServerManager serverManager;
    /**
     * 关闭服务器
     * @param instId
     */
    public void stopServer(String instId){
        if(!canShutDownNow(instId)){
            //如果不能关机就别关
            return;
        }
        ecsAgentService.stopInstanceByEcsId(instId);
    }

    /**
     * 目前是否可以关机
     * @param instId
     * @return
     */
    private boolean canShutDownNow(String instId) {
        //此时没有玩家在线时 可以关机
        Set<String> strings = serverManager.onlinePlayers();
        if (strings.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * 开机 Stoping时开机命令无效，为避免引入复杂度和确保逻辑完整，瞬态都阻塞等待
     * @param instId
     * @return
     */
    public boolean startServer(String instId){
        while (!isTransientStatus(instId)){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ecsAgentService.startInstanceByEcsId(instId);
        return true;
    }

    /**
     * 服务器状态检测
     * Pending和Stopping和Starting三种是瞬态
     * @param instId
     * @return true if TransientStatus
     */
    private boolean isTransientStatus(String instId) {
        List<DescribeInstancesResponse.Instance> instances = ecsAgentService.getAllInstances().getInstances();
        Optional<DescribeInstancesResponse.Instance> any = instances.stream().filter(instance -> instance.getInstanceId().equals(instId)).findAny();
        if(!any.isPresent()){
            //不存在当然不处于瞬态
            return false;
        }
        String status = any.get().getStatus();
        if ("Pending".equalsIgnoreCase(status)){
            return true;
        }else if ("Stopping".equalsIgnoreCase(status)){
            return true;
        }else if ("Starting".equalsIgnoreCase(status)){
            return true;
        }else {
            return false;
        }

    }
}

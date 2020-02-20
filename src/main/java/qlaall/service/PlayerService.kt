package qlaall.service

import org.springframework.stereotype.Service
import qlaall.enums.PlayerStatus
import qlaall.stateful.ServerManager

@Service
class PlayerService {
    fun updateStatus(userName: String, status: PlayerStatus) {
        when(status){
            PlayerStatus.ONLINE-> ServerManager.INSTANCE.playerOnline(userName)
            PlayerStatus.OFFLINE->ServerManager.INSTANCE.playerOffline(userName)
        }
    }

}

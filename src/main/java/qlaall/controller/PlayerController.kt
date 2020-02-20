package qlaall.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qlaall.enums.PlayerStatus
import qlaall.service.PlayerService

/**
 * @author qlaall
 * @create 2020/2/20
 */
@RestController
@RequestMapping("/player")
class PlayerController {
    @Autowired
    lateinit var playerService: PlayerService
    @PostMapping("/status/online")
    fun updateOnline(@RequestParam("userName")userName:String,
                     @RequestParam("clientId")clientId:String){
        playerService.updateStatus(userName, PlayerStatus.ONLINE)

    }

}
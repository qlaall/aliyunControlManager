package qlaall.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import qlaall.stateful.ServerManager

/**
 * @author qlaall
 * @create 2020/2/20
 */
@RestController
@RequestMapping("/player")
class PlayerController {
    @Autowired
    lateinit var serverManager: ServerManager
    @PostMapping("/status/online")
    fun online(@RequestParam("userName")userName:String,
                     @RequestParam("uuid")uuid:String,
               @RequestParam("character")character:String){
        serverManager.setPlayerOnline(userName)
    }
    @PostMapping("/status/quit")
    fun quit(@RequestParam("userName")userName:String,
             @RequestParam("uuid")uuid:String,
                     @RequestParam("character")character:String){
        serverManager.playerOffline(userName)
    }
    @GetMapping("/list")
    fun listOnlineNow(): Set<String> {
        return serverManager.onlinePlayers()
    }
}
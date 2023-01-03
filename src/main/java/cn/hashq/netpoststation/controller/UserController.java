package cn.hashq.netpoststation.controller;

import cn.hashq.netpoststation.dto.LoginDTO;
import cn.hashq.netpoststation.service.UserService;
import cn.hashq.netpoststation.vo.ServerResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ServerResponse login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }
}

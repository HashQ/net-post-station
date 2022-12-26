package cn.hashq.netpoststation.controller;

import cn.hashq.netpoststation.service.ClientManagerService;
import cn.hashq.netpoststation.vo.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/client")
public class ClientController {


    @Resource
    private ClientManagerService clientManagerService;

    @GetMapping("")
    public ServerResponse listClient() {
        return clientManagerService.listClient();
    }

}

package cn.hashq.netpoststation.service;

import cn.hashq.netpoststation.dto.LoginDTO;
import cn.hashq.netpoststation.vo.ServerResponse;

public interface UserService {

    ServerResponse login(LoginDTO loginDTO);
}

package com.mulesoft.server;

import java.net.URL;

import javax.management.MBeanServer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josvaldor.module.Module;
import com.josvaldor.module.mbean.statemachine.StateMachine;

@RestController
public class Server extends StateMachine{
	public Server() {
	}

	public Server(MBeanServer mBeanServer) {
		super(new URL[0], mBeanServer);
	}

	public Server(int id, Module module) {
		super(Integer.valueOf(id), module);
	}
	
	 @RequestMapping("/")
	    public String index() {
	        return "Greetings from Spring Boot!";
	    }
}

package com.onlytp.mod;


import com.onlytp.mod.command.OTPCommand;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;



@Mod(OnlyTP.MOD_ID)
public class OnlyTP {


    public static final String MOD_ID = "onlytp";


    public OnlyTP(){

    }



    @Mod.EventBusSubscriber(
            modid = MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static class ForgeEvents{


        @SubscribeEvent
        public static void registerCommands(
                RegisterCommandsEvent event
        ){

            OTPCommand.register(
                    event.getDispatcher()
            );

        }


    }

}
package com.onlytp.mod.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.arguments.DoubleArgumentType;


import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;

import net.minecraft.core.registries.Registries;

import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.level.Level;



public class OTPCommand {



    /*
     * 维度TAB补全
     */
    private static final SuggestionProvider<CommandSourceStack>
            DIMENSION_SUGGESTIONS =
            (context,builder)->{


                return SharedSuggestionProvider.suggest(

                        context.getSource()
                                .getServer()
                                .levelKeys()
                                .stream()
                                .map(key -> key.location().toString()),

                        builder

                );


            };





    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ){


        dispatcher.register(

                Commands.literal("otp")


                        .then(

                                Commands.argument(
                                                "x",
                                                DoubleArgumentType.doubleArg()

                                        )

                                        .then(

                                                Commands.argument(
                                                                "y",
                                                                DoubleArgumentType.doubleArg()
                                                        )

                                                        .then(

                                                                Commands.argument(
                                                                                "z",
                                                                                DoubleArgumentType.doubleArg()
                                                                        )


                                                                        // 当前维度
                                                                        .executes(ctx -> {


                                                                            return teleport(

                                                                                    ctx.getSource(),

                                                                                    DoubleArgumentType.getDouble(ctx,"x"),
                                                                                    DoubleArgumentType.getDouble(ctx,"y"),
                                                                                    DoubleArgumentType.getDouble(ctx,"z"),

                                                                                    null
                                                                            );


                                                                        })



                                                                        // 指定维度
                                                                        .then(

                                                                                Commands.argument(
                                                                                                "dimension",
                                                                                                ResourceLocationArgument.id()
                                                                                        )

                                                                                        .suggests(DIMENSION_SUGGESTIONS)


                                                                                        .executes(ctx -> {


                                                                                            return teleport(

                                                                                                    ctx.getSource(),

                                                                                                    DoubleArgumentType.getDouble(ctx,"x"),
                                                                                                    DoubleArgumentType.getDouble(ctx,"y"),
                                                                                                    DoubleArgumentType.getDouble(ctx,"z"),

                                                                                                    ResourceLocationArgument
                                                                                                            .getId(ctx,"dimension")
                                                                                                            .toString()

                                                                                            );


                                                                                        })

                                                                        )


                                                        )

                                        )

                        )

        );


    }





    private static int teleport(

            CommandSourceStack source,

            double x,
            double y,
            double z,

            String dimension

    ){


        if(!(source.getEntity()
                instanceof ServerPlayer player)){


            source.sendFailure(
                    Component.literal(
                            "Only player can use this command"
                    )
            );

            return 0;

        }




        try{




            ServerLevel targetLevel;


// 没输入维度，使用玩家当前维度
            if(dimension == null){


                targetLevel = player.serverLevel();


            }

            else {


                ResourceLocation location =
                        new ResourceLocation(dimension);


                ResourceKey<Level> key =
                        ResourceKey.create(
                                Registries.DIMENSION,
                                location
                        );


                targetLevel =
                        player.server.getLevel(key);



                if(targetLevel == null){

                    source.sendFailure(
                            Component.literal(
                                    "Dimension not found: "
                                            + dimension
                            )
                    );

                    return 0;

                }

            }





            if(player.serverLevel() != targetLevel){


                player.teleportTo(
                        targetLevel,
                        x,
                        y,
                        z,
                        player.getYRot(),
                        player.getXRot()
                );


            }
            else{


                player.connection.teleport(
                        x,
                        y,
                        z,
                        player.getYRot(),
                        player.getXRot()
                );


            }



            source.sendSuccess(

                    ()->Component.literal(

                            "Teleport success: "
                                    +x+" "
                                    +y+" "
                                    +z

                    ),

                    false

            );


            return 1;



        }catch(Exception e){


            source.sendFailure(

                    Component.literal(
                            "Invalid coordinate"
                    )

            );


            return 0;

        }


    }


}
package keydisparchd.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;
import keydisparchd.common.CmdSet;
import keydisparchd.common.Constants;
import keydisparchd.model.CmdAndKey;
import keydisparchd.server.Server;

import java.util.List;
import java.util.Random;

public class RedisClientHandler extends ChannelDuplexHandler {


    private List<Server> serverList;


    public RedisClientHandler(List<Server> serverList) {
        this.serverList = serverList;
    }

    /**
     * 将redis的响应发送给redis-cli
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.writeAndFlush(msg);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String stringCmd = byteBuf.toString(CharsetUtil.UTF_8);
        int serverCount = serverList.size();
        Server server = null;
        int serverIdx = 0;
        // 解析命令，算出key等信息
        CmdAndKey cmdAndKey = resolveStringCmd(stringCmd);
        System.out.println("cmdAndKey = " + cmdAndKey);
        if (cmdAndKey == null) {
            // 随机发送
            serverIdx = new Random().nextInt(serverCount);
            server = serverList.get(serverIdx);
        } else if (CmdSet.isOtherCmd(cmdAndKey.getCmd())) {
            String key = cmdAndKey.getKey();
            // 随机发送
            serverIdx = new Random().nextInt(serverCount);
            server = serverList.get(serverIdx);
        } else if (CmdSet.isNormorCmd(cmdAndKey.getCmd())) {
            // 根据key的hash值发送
            String key = cmdAndKey.getKey();
            serverIdx = key.hashCode() % serverCount;
            server = serverList.get(serverIdx);
        }

        System.out.println("serverIdx = " + serverIdx);

        // 发给server,把client的channel通过attr属性带过去
        server.channel().attr(Constants.attributeKey).set(ctx.channel());
        server.channel().writeAndFlush(msg);
    }

    /*
       规则：目的是为了解析出key
       如果key是monnitor、info、keys等命令，则随机挑选出一个server发送
       如果是一般的key，则根据hash选择server发送
     */
    private CmdAndKey resolveStringCmd(String stringCmd) {
        stringCmd = stringCmd.toLowerCase();
        System.out.println("stringCmd = " + stringCmd.replace(Constants.ENTRY, ""));
        String[] cmdArr = stringCmd.split(Constants.ENTRY);
        String key = "";
        // info ==> *1$4info
        if (Constants.X1.equalsIgnoreCase(cmdArr[0])) {
            key = cmdArr[2];
            return new CmdAndKey(key, null);
        }

        // keys * ==> *2$4keys$1*
        if (stringCmd.startsWith("*2") && Constants.KEYS.equalsIgnoreCase(cmdArr[2])) {
            return new CmdAndKey(Constants.KEYS, null);
        }

        int index = 0;
        for (String cmd : cmdArr) {
            if (cmd.startsWith(Constants.$)) {
                return new CmdAndKey(cmdArr[index + 1], cmdArr[index + 3]);
            }
            index++;
        }

        return null;

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 这里如果是jedis关闭连接后，会抛异常
        // System.err.print("exceptionCaught: ");
        // cause.printStackTrace(System.err);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connection client active:" + ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connention client inactive:" + ctx.channel().id());
    }
}

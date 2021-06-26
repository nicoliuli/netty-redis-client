package keydisparchd.common;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class Constants {
    public static final AttributeKey<Channel> attributeKey = AttributeKey.newInstance("client-channel");
}

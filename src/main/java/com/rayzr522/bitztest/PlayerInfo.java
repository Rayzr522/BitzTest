
package com.rayzr522.bitztest;

import org.bukkit.util.Vector;

import com.rayzr522.bitzapi.config.data.ISerializable;
import com.rayzr522.bitzapi.config.data.Serialized;

public class PlayerInfo implements ISerializable {

    @Override
    public void onDeserialize() {
    }

    @Override
    public void onPreSerialize() {
    }

    @Serialized
    public double  money   = 10.0;

    @Serialized
    public boolean godMode = false;

    @Serialized
    public String  nickName;

    @Serialized
    public String  uuid;

    @Serialized
    public Vector  pos;

    @Override
    public String toString() {
        return "PlayerInfo [money=" + money + ", godMode=" + godMode + ", nickName=" + nickName + ", uuid=" + uuid + ", pos=" + pos + "]";
    }

}

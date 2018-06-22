package man10.red.mindbattlenumber;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
////////////////////////////////////////////////
//  OP用のコマンド　メソッド
//  permission "MindBattleNumber.op.<コマンド名>"
////////////////////////////////////////////////
public class MBN_OPcommands {
    private MindBattleNumber plugin;
    private MBN_commands_sub sub;

    public MBN_OPcommands(MindBattleNumber plugin, MBN_commands_sub sub) {
        this.plugin = plugin;
        this.sub = sub;
    }

    ////////////////////////////////////////
    //  gamemode ON / 起動
    ///////////////////////////////////////
    public void ON(Player p) {
        if (!(p.hasPermission("MindBattleNumber.op.on"))) {
            p.sendMessage(plugin.PluginName+"§4§l権限がありません!");
            return;
        }
        if(plugin.game_mode!=2){
            p.sendMessage(plugin.PluginName+"§4§lすでにONです!");
            return;
        }

        plugin.game_mode = 0;
        p.sendMessage(plugin.PluginName + "§a§lONにしました!");
    }

    ////////////////////////////////////////
    //  gamemode OFF / 強制終了
    ///////////////////////////////////////
    public void OFF(Player p) {
        if (!(p.hasPermission("MindBattleNumber.op.off"))) {
            p.sendMessage(plugin.PluginName+"§4§l権限がありません!");
            return;
        }
        p.sendMessage(plugin.PluginName + "§a§lOFFにしました!");
        if (plugin.game_mode == 1) {
            Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§4§l強制終了のため返金します");
            for (int i = 0; i != 101; i++) {
                if (!(plugin.joinPlayerlist.get(i).equalsIgnoreCase("-"))) {
                    plugin.deposit(Bukkit.getPlayer(UUID.fromString(plugin.joinPlayerlist.get(i))), (double) plugin.game_joinMoney);
                }
            }
        }
        sub.reset();
        plugin.game_mode = 2;
    }

////////////////////////////////////////////////////////////////////////////
// Now result / 今の状況
////////////////////////////////////////////////////////////////////////////
    public void now(Player p){
        if(!(p.hasPermission("MindBattleNumber.op.now"))) {
            p.sendMessage(plugin.PluginName+"§4§l権限がありません!");
            return;
        }
        if(plugin.game_mode!=1){
            Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§4§l今はゲームが開催されていません!");
            return;
        }
        p.sendMessage("§e§l§m=§e§l[§f§l今の勝負の状況§e§l]§e§l§m=====================");
        List<Map.Entry<String, Integer>> list_entries = new ArrayList<Map.Entry<String, Integer>>(plugin.player_number.entrySet());
        Collections.sort(list_entries, new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2)
            {
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });
        int i=0;
        for(Map.Entry<String, Integer> entry : list_entries) {
            i+=1;
            p.sendMessage("§e§l"+entry.getKey() + " §f§l-> §b§l" + entry.getValue());
        }
        if(i==0){
            p.sendMessage("§4§l誰も参加していません!");
        }

    }
}

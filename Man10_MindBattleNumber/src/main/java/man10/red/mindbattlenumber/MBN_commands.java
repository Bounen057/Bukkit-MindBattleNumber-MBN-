package man10.red.mindbattlenumber;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
////////////////////////////////////////////////
//  一般人用のコマンド　メソッド
////////////////////////////////////////////////
public class MBN_commands{
    private MindBattleNumber plugin;
    private MBN_commands_sub sub;

    public MBN_commands(MindBattleNumber plugin,MBN_commands_sub sub) {
        this.plugin = plugin;
        this.sub = sub;
    }
////////////////////////////////////////////////////////////////////////////
// Commands: game help / ゲームの詳細 コマンド
////////////////////////////////////////////////////////////////////////////
    public void help(Player p) {
        p.sendMessage("§e§l§m-§d§l[MBN help]§e§l§m-------------------------------");
        p.sendMessage("§7§l数字を選び、特定の範囲の数字で1番大きい");
        p.sendMessage("§7§l数を言った人が勝ち。しかし他人と同じ数字を言うと");
        p.sendMessage("§7§l負けになります。慎重に数字を選ぼう!");
        p.sendMessage("§7/mbn start <賭け金額> §3§l-ゲームを開催します");
        p.sendMessage("§7/mbn join <数字> §3§l-開催中のゲームに参加します");
        p.sendMessage("§7/mbn result §3§l-前回の結果を見ます");
        p.sendMessage("§7/mbn withdraw §3§l-不正に終了した分のゲームのお金を貰います");
        if(p.hasPermission("MindBattleNumber.op")){
            p.sendMessage("§7/mbn off §5§l-ゲームをOFFにします(OP用)");
            p.sendMessage("§7/mbn on §5§l-ゲームをONにします(OP用)");
            p.sendMessage("§7/mbn now §5§l-今の状況(ゲーム開催中のみ OP用)");
        }
        if (plugin.game_mode == 1) {
            p.sendMessage("§e§l§m-§f§l[現在の詳細]§e§l§m-----------------------------");
            p.sendMessage("§7§l残り時間:" + plugin.game_time + "秒 賭け金額:" + plugin.game_joinMoney + "¥");
            p.sendMessage("§7§lプレイヤー数:" + plugin.game_playerAmount +" §7§l数字:1~"+plugin.game_maxNumber);
        }
    }

////////////////////////////////////////////////////////////////////////////
// Commands: game start / ゲーム開催処理
////////////////////////////////////////////////////////////////////////////
    public void start(Player p, String args) {
        /////////////////////
        // エラーチェック
        /////////////////////
        for (int i = 0; i != 127; i++) {
            plugin.joinPlayerlist.add("-");
        }
        try {
            plugin.game_joinMoney = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            p.sendMessage("§d§l[§f§lMBN§a§l]§c§l数字にしてください!");
            return;
        }
        if (plugin.game_mode == 1) {
            p.sendMessage(plugin.PluginName + "§c§lもう既に開催されています");
            return;
        }
        if (plugin.game_mode == 2) {
            p.sendMessage(plugin.PluginName + "§c§l現在はCLOSE中です");
            return;
        }
        if(plugin.game_joinMoney < 10000){
            p.sendMessage("§d§l[§f§lMBN§a§l]§c§l10,000円以上にしてください!");
            return;
        }
        if(plugin.game_joinMoney > 1000000000){
            p.sendMessage("§d§l[§f§lMBN§a§l]§c§l1,000,000,000円以下にしてください!");
            return;
        }
        //////////////////////////
        //  ゲーム開催 処理
        //////////////////////////
        //   最大数の処理
        plugin.game_maxNumber = (int)(plugin.game_beforePlayerAmount*5/4);
        if(plugin.game_maxNumber <= 5){
            plugin.game_maxNumber=5;
        }

        //   変数の準備
        plugin.game_time = 60;
        plugin.player_number.clear();
        plugin.win_before = null;
        plugin.joinPlayerlist.add(p.getUniqueId().toString());
        plugin.game_mode = 1;

        //   メッセージ
        Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§f§l" + p.getName() + "§e§lが賭け金:" + plugin.game_joinMoney + "円 §3§l数字:1~"+plugin.game_maxNumber+" のMBNを開催しました![/mbn]");
    }

////////////////////////////////////////////////////////////////////////////
// Commands: game join / ゲーム参加処理
////////////////////////////////////////////////////////////////////////////
    public void join(Player p, String args) {
        /////////////////////
        //  エラーチェック
        /////////////////////
        //  ゲームが開催されているかチェック
        if (plugin.game_mode != 1) {
            p.sendMessage(plugin.PluginName + "§c§l今はゲームが開催されていません!");
            return;
        }
        //   入力した数字をint型にする
        try {
            plugin.game_number = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            p.sendMessage("§d§l[§f§lMBN§a§l]§c§l1~"+plugin.game_maxNumber+"の数字にしてください!");
            return;
        }
        //   もうすでに数字を選んでいるかチェック
        for (int i = 0; i != 127; i++) {
            if (plugin.joinPlayerlist.get(i).equalsIgnoreCase(p.getUniqueId().toString())) {
                p.sendMessage(plugin.PluginName + "§c§lあなたはもう既に数字を選んでいます!");
                return;
            }
        }
        //   数字の判定
        if (!(plugin.game_number <= plugin.game_maxNumber && plugin.game_number > 0)) {
            p.sendMessage(plugin.PluginName + "§c§l1~"+plugin.game_maxNumber+"の数字にしてください!");
            return;
        }
        //   お金を持っているかチェック
        if (plugin.get(p) < plugin.game_joinMoney) {
            p.sendMessage(plugin.PluginName + "§c§l" + plugin.game_joinMoney + "円以上あなたは持っていません!");
            return;
        }

        /////////////////////////////
        //   ゲーム join メイン処理
        ////////////////////////////
        //  敗北判定
        if (!(plugin.game_JoinNumberPlayer[plugin.game_number]==null)) {
            plugin.game_lossNumber[Integer.parseInt(args)] = 1;
        }

        //  変数の準備
        plugin.setConfig(plugin.getConfig("data."+p.getUniqueId()+".money")+plugin.game_joinMoney,"data."+p.getUniqueId()+".money");
        plugin.game_playerAmount += 1;
        plugin.joinPlayerlist.set(plugin.game_playerAmount,p.getUniqueId().toString());
        plugin.game_JoinNumberPlayer[plugin.game_number] = p.getUniqueId().toString();
        plugin.withraw(p, (double) plugin.game_joinMoney);
        plugin.player_number.put(p.getName(),plugin.game_number);

        //  メッセージ
        Bukkit.broadcastMessage(plugin.PluginName+"§e§l"+p.getName()+"さん§f§lが参加しました! 現在:"+plugin.game_playerAmount+"人");
        p.sendMessage(plugin.PluginName + "§b§l数字:" + plugin.game_number + "を選びました!");
    }

////////////////////////////////////////////////////////////////////////////
// Commands: game finish / ゲーム終了処理
////////////////////////////////////////////////////////////////////////////
    public void finish() {
        int game_amountWinner = 0;
        plugin.game_winPlayer = null;
        ////////////////////////////////////
        //勝者判定
        for (int i = 0; i != plugin.game_maxNumber+1; i++) {
            if ((!(plugin.game_JoinNumberPlayer[i] == null)) && plugin.game_lossNumber[i] == 0) {
                plugin.game_winPlayer = plugin.game_JoinNumberPlayer[i];
                game_amountWinner = i;
            }
            if ((!(plugin.game_JoinNumberPlayer[i] == null) )) {
                if (!(plugin.joinPlayerlist.get(i).equalsIgnoreCase("-"))) {
                    plugin.setConfig(plugin.getConfig("data." + UUID.fromString(plugin.joinPlayerlist.get(i)) + ".money") - plugin.game_joinMoney, "data." + UUID.fromString(plugin.joinPlayerlist.get(i)) + ".money");
                }
            }
        }
        //////////////////////////////////
        //結果
        if (plugin.game_playerAmount == 1) {
            Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§4§l参加者が1人のため返金します。");
            for (int i = 0; i != plugin.game_maxNumber+1; i++) {
                if (!(plugin.joinPlayerlist.get(i).equalsIgnoreCase("-"))) {
                    plugin.deposit(Bukkit.getPlayer(UUID.fromString(plugin.joinPlayerlist.get(i))), (double) plugin.game_joinMoney);
                    plugin.player_number_before = plugin.player_number;
                    plugin.win_before = "§4§l1人だった";
                }
            }
            plugin.game_beforePlayerAmount=plugin.game_playerAmount;
            sub.reset();
            return;
        }

        if (plugin.game_winPlayer == null) {
            Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§4§l該当者がいなかったので返金します");
            for (int i = 0; i != plugin.game_maxNumber+1; i++) {
                if (!(plugin.joinPlayerlist.get(i).equalsIgnoreCase("-"))) {
                    plugin.deposit(Bukkit.getPlayer(UUID.fromString(plugin.joinPlayerlist.get(i))), (double) plugin.game_joinMoney);
                }
                plugin.game_beforePlayerAmount=plugin.game_playerAmount;
                plugin.player_number_before = plugin.player_number;
                plugin.win_before = "§4§l該当者がいなかった";
            }
            sub.reset();
            return;
        }
        /////////////////////////////////////////
        //   勝利者が決まった時の判定
        /////////////////////////////////////////
        //  変数の処理
        plugin.game_beforePlayerAmount=plugin.game_playerAmount;
        plugin.setConfig(plugin.getConfig("data."+Bukkit.getOfflinePlayer(UUID.fromString(plugin.game_winPlayer)).getUniqueId()+".money")-plugin.game_joinMoney,"data."+Bukkit.getOfflinePlayer(UUID.fromString(plugin.game_winPlayer)).getUniqueId()+".money");
        plugin.player_number_before = plugin.player_number;

        //  勝利時のお金の処理
        double game_winMoney = plugin.game_joinMoney * plugin.game_playerAmount;
        plugin.deposit(Bukkit.getPlayer(UUID.fromString(plugin.game_winPlayer)), game_winMoney);
        Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§6§l§n勝者:" + Bukkit.getOfflinePlayer(UUID.fromString(plugin.game_winPlayer)).getName() + "§b§l 数字:" + game_amountWinner + " §e§l獲得金額:" + game_winMoney + "円");

        plugin.win_before = "§6§l§n勝者:" + Bukkit.getOfflinePlayer(UUID.fromString(plugin.game_winPlayer)).getName() + "§b§l 数字:" + game_amountWinner + " §e§l獲得金額" + game_winMoney + "円";
        sub.reset();
    }


    ////////////////////////////////////////
    //  result / 前回の結果表示
    ///////////////////////////////////////
    public void result(Player p){
        p.sendMessage("§e§l§m=§e§l[§f§l前回の勝負の結果§e§l]§e§l§m=====================");
        if(!(plugin.win_before==null)){p.sendMessage(plugin.win_before);}
        List<Map.Entry<String, Integer>> list_entries = new ArrayList<Map.Entry<String, Integer>>(plugin.player_number_before.entrySet());
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
            p.sendMessage("§4§l前回の参加者無し");
        }
    }


    ////////////////////////////////////////////////
    //  withdraw / 不正に終わったゲームのお金を受け取る
    ////////////////////////////////////////////////
    public void withdraw(Player p){
        double money = plugin.getConfig("data."+p.getUniqueId()+".money");
        if(plugin.game_mode==1){
            p.sendMessage(plugin.PluginName+"§c§lゲームが開催されてない時に実行してください!");
            return;
        }
        if(money==0){
            p.sendMessage(plugin.PluginName+"§c§lあなたの不正に終了したゲーム分のお金はありません!");
            return;
        }
        p.sendMessage(plugin.PluginName+"§f§l§n金額->"+plugin.getConfig("data."+p.getUniqueId()+".money")+"円§a§l見つかりました!");
        p.sendMessage(plugin.PluginName+"§a§lあなたに"+(int)money+"円渡しました!");
        plugin.setConfig(0,"data."+p.getUniqueId()+".money");
        plugin.deposit(p,money);
    }
}

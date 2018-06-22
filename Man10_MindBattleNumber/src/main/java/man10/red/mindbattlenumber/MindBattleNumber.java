package man10.red.mindbattlenumber;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/*
  PluginName: MindBattleNumber
  Created by Bounen057 6/20 20:00~
  Supporter by takatronix
 */

////////////////////////////////////////////////
//  メイン　メソッド
//  全体の流れは全てここ
////////////////////////////////////////////////
public class MindBattleNumber extends JavaPlugin {
    ///////////////////////////////////////////
    //  変数 宣言
    ///////////////////////////////////////////
    public String PluginName = "§d§l[§f§lMBN§a§l]";//メッセージ用
    int game_mode = 0;//ゲームのモード 0:未開催 1:開催中 2:off
    public int game_joinMoney = 0;//ゲーム 参加費
    public int game_number = 0;//ゲーム プレイヤーの数字
    public int game_playerAmount = 0;//ゲーム 参加人数
    public String game_winPlayer;//ゲーム 勝者
    public int game_maxNumber;//ゲーム 最大の数
    public int game_beforePlayerAmount=0;//前回のゲームの人数


    public String[] game_JoinNumberPlayer = new String[102];//ゲーム プレイヤーの数字
    public int[] game_lossNumber = new int[102];//ゲーム 敗北確定数字

    public String win_before;
    public HashMap<String,Integer> player_number_before = new HashMap<String,Integer>();
    public HashMap<String,Integer> player_number = new HashMap<String,Integer>();
    public ArrayList<String> joinPlayerlist = new ArrayList<>();//参加してるプレイヤーのUUID
    public int game_time = 0;//ゲームの残り時間

    CustomConfig stats;
    MBN_commands_sub mbn_commands_sub = new MBN_commands_sub(this);
    MBN_commands mbn_commands = new MBN_commands(this,mbn_commands_sub);
    MBN_OPcommands mbn_OPcommands = new MBN_OPcommands(this,mbn_commands_sub);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player p = (Player)sender;
        if(args.length==0){mbn_commands.help(p);return false;}
        if(args.length!=1 && (args[0].equalsIgnoreCase("help"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn help");return false;}
        if(args[0].equalsIgnoreCase("help")){ mbn_commands.help(p);return false;}
        if(args.length!=2 && (args[0].equalsIgnoreCase("start"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn start <賭け金額>");return false;}
        if(args[0].equalsIgnoreCase("start")){mbn_commands.start(p,args[1]);return false;}
        if(args.length!=2 && (args[0].equalsIgnoreCase("join"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn join <1~"+game_maxNumber+">");return false;}
        if(args[0].equalsIgnoreCase("join")){ mbn_commands.join(p,args[1]);return false;}
        if(args.length!=1 && (args[0].equalsIgnoreCase("result"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn result");return false;}
        if(args[0].equalsIgnoreCase("result")){ mbn_commands.result(p);return false;}
        if(args.length!=1 && (args[0].equalsIgnoreCase("withdraw"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn withdraw");return false;}
        if(args[0].equalsIgnoreCase("withdraw")){ mbn_commands.withdraw(p);return false;}

        if(args.length!=1 && (args[0].equalsIgnoreCase("off"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn off");return false;}
        if(args[0].equalsIgnoreCase("off")){ mbn_OPcommands.OFF(p);return false;}
        if(args.length!=1 && (args[0].equalsIgnoreCase("on"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn on");return false;}
        if(args[0].equalsIgnoreCase("on")){ mbn_OPcommands.ON(p);return false;}
        if(args.length!=1 && (args[0].equalsIgnoreCase("now"))){ p.sendMessage(PluginName + "§c§l正しい使い方:/mbn now");return false;}
        if(args[0].equalsIgnoreCase("now")){ mbn_OPcommands.now(p);return false;}
        p.sendMessage(PluginName + "§c§l/mbn help");


        return false;
    }

    @Override
    public void onEnable() {
        //files
        stats = new CustomConfig(this, "stats.yml");
        stats.saveDefaultConfig();
        //commands
        getCommand("mbn").setExecutor(this);
        //timer
        TimerTask task = new TimerTask() {
            public void run() {
                timer();
            }
        };

        java.util.Timer timer = new Timer();
        timer.schedule(task, 1000L, 1000L);
    }

    @Override
    public void onDisable() {
    }
    ///////////////////////////////////////////////////////////
    // 定期処理メソッド/Timer Method
    ///////////////////////////////////////////////////////////
    public void timer(){
        ///////////////////
        //  エラーチェック
        if(game_mode!=1){return;}
        game_time -= 1;
        switch (game_time){
            case 30:
            case 5:
            case 3:
            case 2:
            case 1:
                Bukkit.broadcastMessage(PluginName+"§f§l残り:" + game_time + "秒... 人数:" + game_playerAmount);break;
            case 0:
                Bukkit.broadcastMessage("§d§l[§f§lMBN§a§l]§e§l終了!結果は...§f§l§kaaa");
        }
        //////////////////////////////////////////
        //  ゲームの終了処理
        if(game_time==-4){
            mbn_commands.finish();
        }

    }

    ///////////////////////////////////////////////////////////
    // 他のクラスでもVaultが使えるようメソッド/Vaults Method
    ///////////////////////////////////////////////////////////
    public void show(Player p){
        new VaultManager(this).showBalance(p.getUniqueId());
    }
    public double get(Player p){
        return new VaultManager(this).getBalance(p.getUniqueId());
    }
    public void withraw(Player p,Double money){
        new VaultManager(this).withdraw(p.getUniqueId(),money);
    }
    public void deposit(Player p,Double money){
        new VaultManager(this).deposit(p.getUniqueId(),money);
    }
    public void setConfig(int i,String str){
        stats.getConfig().set(str,i);
        stats.saveConfig();
    }
    public int getConfig(String str){
        return stats.getConfig().getInt(str);
    }
}

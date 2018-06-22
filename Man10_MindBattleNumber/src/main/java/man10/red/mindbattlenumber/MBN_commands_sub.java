package man10.red.mindbattlenumber;

////////////////////////////////////////////////
//  MBN_commands and MBN_OPcommands のサブクラス
//  メソッド をここに置いています。
////////////////////////////////////////////////

public class MBN_commands_sub {
    private MindBattleNumber plugin;
    public MBN_commands_sub(MindBattleNumber plugin) {
        this.plugin = plugin;
    }
    /////////////////////////////////////////////
    //  変数初期化(ゲーム初期化)
    /////////////////////////////////////////////
    public void reset(){

        for (int i = 0; i != 101; i++) {
            plugin.game_JoinNumberPlayer[i] = null;
            plugin.game_lossNumber[i] = 0;
        }

        plugin.joinPlayerlist.clear();

        plugin.game_joinMoney = 0;
        plugin.game_mode = 0;
        plugin.game_winPlayer = null;
        plugin.game_playerAmount = 0;
    }
}

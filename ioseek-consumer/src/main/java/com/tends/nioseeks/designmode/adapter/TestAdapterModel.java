package com.tends.nioseeks.designmode.adapter;

//适配器模式（Adapter Pattern）：作为两个不兼容的接口之间的桥梁。这种类型的设计模式属于结构型模式，它结合了两个独立接口的功能
//例：读卡器是内存卡和笔记本之间的适配器。将内存卡插入读卡器，再将读卡器插入笔记本，这样就可以通过笔记本来读取内存卡
public class TestAdapterModel {
    //主要解决在软件系统中，常常要将一些"现存的对象"放到新的环境中，而新环境要求的接口是现对象不能满足的
    //使用场景：有动机地修改一个正常运行的系统的接口，这时应该考虑使用适配器模式。
    //注意事项：适配器不是在详细设计时添加的，而是解决正在服役的项目的问题


    interface AdvancedMediaPlayer {
        public void playVlc(String fileName);
        public void playMp4(String fileName);
    }
    static class VlcPlayer implements AdvancedMediaPlayer{
        @Override
        public void playVlc(String fileName) {
            System.out.println("Playing vlc file. Name: "+ fileName);
        }
        @Override
        public void playMp4(String fileName) { }
    }
    static class Mp4Player implements AdvancedMediaPlayer{
        @Override
        public void playVlc(String fileName) { }
        @Override
        public void playMp4(String fileName) {
            System.out.println("Playing mp4 file. Name: "+ fileName);
        }
    }

    interface MediaPlayer {
        public void play(String audioType, String fileName);
    }
    static class MediaAdapter implements MediaPlayer {
        AdvancedMediaPlayer advancedMusicPlayer;

        public MediaAdapter(String audioType){
            if(audioType.equalsIgnoreCase("vlc") ){
                advancedMusicPlayer = new VlcPlayer();
            } else if (audioType.equalsIgnoreCase("mp4")){
                advancedMusicPlayer = new Mp4Player();
            }
        }
        @Override
        public void play(String audioType, String fileName) {
            if(audioType.equalsIgnoreCase("vlc")){
                advancedMusicPlayer.playVlc(fileName);
            }else if(audioType.equalsIgnoreCase("mp4")){
                advancedMusicPlayer.playMp4(fileName);
            }
        }
    }
    static class AudioPlayer implements MediaPlayer {
        MediaAdapter mediaAdapter;

        @Override
        public void play(String audioType, String fileName) {
            //播放 mp3 音乐文件的内置支持
            if (audioType.equalsIgnoreCase("mp3")) {
                System.out.println("Playing mp3 file. Name: " + fileName);
            }
            //mediaAdapter 提供了播放其他文件格式的支持
            else if (audioType.equalsIgnoreCase("vlc")
                    || audioType.equalsIgnoreCase("mp4")) {
                mediaAdapter = new MediaAdapter(audioType);
                mediaAdapter.play(audioType, fileName);
            } else {
                System.out.println("Invalid media. "+ audioType + " format not supported");
            }
        }
    }



    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.play("mp3", "beyond the horizon.mp3");
        audioPlayer.play("mp4", "alone.mp4");
        audioPlayer.play("vlc", "far far away.vlc");
        audioPlayer.play("avi", "mind me.avi");
    }
    
}

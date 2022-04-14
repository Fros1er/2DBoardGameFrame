# 音乐播放器

AudioPlayer类提供两个static方法播放音乐。支持wav, mp3, ogg格式（也可能有其他的）。

`public static Future<?> playBgm(String path);`  
播放背景音乐，一直循环。

返回一个Future<?>，和线程有关，是用来停止播放的。需要的时候调用future.cancel(true)就好。

建议写在main函数里面。

`public static Future<?> playSound(String path);`  
同上，但播完停止。可以用来播放音效。

建议写在Action返回SUCCEED或者PENDING的时候。

`Stage.instance().setBgm(String path)`  
此外，所有Stage提供一个setBgm方法。通过这个设置背景音乐后，在进入该Stage时会开始播放背景音乐并循环，退出时停止。
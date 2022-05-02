# 一些基础知识

本节主要介绍部分jvav的语法和一点点设计模式的一小部分，包括lambda表达式，匿名内部类，单例模式。如果您已经有所了解，可以选择跳过。

这一块懂个大概就好，不需要完全掌握也能写。根据其他人的经验，对着example改好像也能改出来...？

建议阅读本节时和example的源码里语法不懂的部分做对照。

本节内容参考：  
https://www.runoob.com/w3cnote/java-inner-class-intro.html  
https://www.runoob.com/java/java8-lambda-expressions.html

我的语文水平有限，而且对java的理解也有限，所以没看懂可以去网上查查，网上讲的肯定比我清楚（仅限本节）。

另外，本文的函数完全可以理解为方法，这个好像只是我叫法的问题，其实指的是一种东西的。

# 匿名内部类

如果没看懂建议回去复习下继承啥的（

直接举个例子。这是swing的监听器：
``` java
btn.addActionListener();
```
这个监听器需要传入一个ActionListener接口。
但是，接口是不能被构造的（抽象类也是）。

所以，一般的做法是先继承一个类出来：
``` java
class myListener implements ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        //do sth
    }
}
```
然后传进去：
``` java
btn.addActionListener(new myListener);
```

这样写多少有点麻烦，因为一般来说每个Listener都只用在一个地方。所以在不怎么需要重复构造一个类的时候，有一个简单一点的写法：

``` java
btn.addActionListener(new ActionListener(/*如果继承的类构造方法有参数，写在这*/) {
    @Override
    public void actionPerformed(ActionEvent e) {
        //do sth
    }
}
```

这两段代码做的事情基本一样，除了后者没有给类起名字。  
这就是匿名内部类。在使用框架的时候有几个地方用它比较方便。

匿名内部类和普通的类一样，也可以定义成员变量。这一部分会在下面详细讲，具体的例子详见Chess的Action。

# lambda表达式

刚才说的是匿名内部类，现在说的这玩意叫匿名函数，或者更常用的叫法是lambda表达式。

它长这样：
```
(参数) -> {
    函数体
};
```

匿名函数是个。。。没有名字的函数。。。  

框架里有很多函数需要你传入一些奇怪的接口，包括什么Predicate，Consumer，Supplier等等等等。这些接口里面只有一个方法，所以可以把这些接口，
正规来说，函数式接口，直接当成一个方法本身。  

那很显然，这些框架里的函数就是参数为函数的函数。。它们的作用是让你自定义一些行为。框架会在之后的某个时候调用你传进去的这个函数的。

举个例子，`Game.setPlayerWinningJudge((player) -> { /*return some boolean;*/ });`在这里，你通过传入一个函数来定义如何判断玩家胜利。
框架会在每个回合结束的时候调用你传进去的这个函数，如果它返回true，则player（给它的参数，代表某个玩家）胜利。

然后，这些传进去的函数显然只需要在传入的时候写一次，这时候写个lambda表达式进去就行，毕竟匿名函数肯定是个函数。

此外，lambda表达式的参数和返回值绝大多数情况下是不需要手动指定类型名的。如果你返回的不对，idea会给你标红的。

再拿listener举例子说下语法。ActionListener接口里只有一个方法，所以它也能写成lambda表达式：
```
btn.addActionListener((e) -> {
    //do sth;
})
```

框架里很大一部分的代码要用到这里，建议认真看看。

## 捕获

匿名内部类和lambda表达式有个很方便的地方：可以直接读取（但不能修改）外部的变量。具体可以看example里的registerGridAction方法，里面返回匿名内部类的时候直接用了外面的变量。

详细一点说，在构建的时候，如果用到了外面的**非static**变量，jvav会自动把外面变量的值copy一份到你的lambda表达式或者匿名内部类里面。
之后如果外面的变量修改了，里面的也不会变。(如果外面的变量不是基本类型而是类，那copy的是**引用**，外面改了引用对应的实例里面还是会改，反之一样)。

举个例子吧。如果把lambda或者匿名内部类写全，捕获大体上是这么工作的：
```
// 非匿名内部类版本
class myListener implements ActionListener() {
    int capturedVar;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // using captured var 1.
        // do sth
    }
    
    public myListener(int var) {
        super();
        this.capturedVar = var;
    }
}

...

int someVar = 114514;
btn.addActionListener(new myListener(someVar));
```

```
// 匿名内部类版本
int someVar = 114514;
btn.addActionListener(new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
        doSomething(someVar); // 直接用就行
    }
}
```

```
// lambda版本
int someVar = 114514;
btn.addActionListener((e) -> {
    doSomething(someVar); // 还是直接用就行
})
someVar = 1919810; // 这里lambda里面的someVar还是114514
```

有时候idea会提示你`Variable '...' is accessed from within inner class, needs to be final or effectively final`。
这是java的一个奇怪特性。。。用来防止程序员错误的在内部类里试图改外面的变量，所以被捕获的变量必须是final的。这个让idea自己给你改就行。

捕获有什么用呢。。。除了让代码量更少之外，还可以记录状态（和成员变量一样）。具体看Chess。

# 单例模式

有很多时候，你希望你的class只有一份实例。比如说，你肯定不希望你的棋盘莫名其妙的同时出现好几个。  
单例模式可以保证这一点。比如你想拿到GameStage的实例，增加一个作弊按钮的时候，你会发现GameStage的构造函数被禁用了。这时候你可以调用GameStage.instance()方法来获取它的唯一实例。这个是全局的静态函数，所以在哪调都可以。

除了单例，框架里还存在一堆只有static方法和成员的类，比如说Game和View。这些就更方便了，直接像Math.abs()一样调静态方法就可以了。

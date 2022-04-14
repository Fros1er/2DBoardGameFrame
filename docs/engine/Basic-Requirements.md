# 一些基础知识

本节主要介绍部分jvav的语法和一点点设计模式的一小部分，包括lambda表达式，匿名内部类，单例模式和工厂模式。如果您已经有所了解，可以选择跳过。

建议阅读本节时和example里FIR.java的源码里语法不懂的部分做对照。

本节内容参考：  
https://www.runoob.com/w3cnote/java-inner-class-intro.html  
https://www.runoob.com/java/java8-lambda-expressions.html

我的语文水平有限，而且对java的理解也有限，所以没看懂可以去网上查查，网上讲的肯定比我清楚（仅限本节）。
本节内容不一定需要完全弄明白，好像根据其他人的经验，对着例子改也能改出来...？

# 匿名内部类

如果没看懂建议回去复习下继承啥的（

我直接举个例子。这是swing的监听器：
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

btn.addActionListener((e) -> {
    //do sth;
})
```

这两段代码做的事情基本一样，除了后者没有给类起名字。  
这就是匿名内部类。在使用框架的时候有几个地方用它比较方便。

匿名内部类和普通的类一样，也可以定义成员变量。详见Chess的Action。

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
正规来说，函数式接口，直接当成一个函数。  
那很显然，这些函数就是参数为函数的函数。。
那把函数传进去就行了。很多时候这些传进去的函数只需要在传入的时候写一次，这时候写个lambda表达式当函数用就行。

框架里很大一部分的代码要用到这个语法。example里遍地都是，我在这就不举例子了。

## 捕获

匿名内部类和lambda表达式有个很方便的地方：可以直接读取（但不能修改）外部的变量。具体可以看example里的registerGridAction方法，里面返回匿名内部类的时候直接用了外面的变量。

详细一点说，在构建的时候，如果用到了外面的**非static**变量，jvav会自动把外面变量的值copy一份到你的lambda表达式或者匿名内部类里面。
之后如果外面的变量修改了，里面的也不会变。(如果外面的变量不是基本类型而是类，那copy的是引用，外面改了里面还是会改)。

# 单例模式

两个设计模式我只简单说下怎么用。

有很多时候，你希望你的class只有一份实例。比如说，你肯定不希望你的棋盘莫名其妙的同时出现好几个。  
单例模式可以保证这一点。比如你想拿到GameStage的实例，增加一个作弊按钮的时候，你会发现GameStage的构造函数被禁用了。这时候你可以调用GameStage.instance()方法来获取它的唯一实例。这个是全局的静态函数，所以在哪调都可以。

除了单例，框架里还存在一堆只有static方法和成员的类，比如说Game和View。这些就更方便了，直接像Math.abs()一样调就可以了。

# 工厂模式

简单来说，以这个模式实现的方法会像工厂一样“生产”实例。

在框架里，你会看到一些类似setSomething, bindSomething，registerSomething的函数。这些函数需要你以lambda表达式的形式提供一个工厂方法。
框架里需要你这么做的原因在于：它不知道你的格子或者Action是怎么定义的，需要你根据一些参数来返回一个实例，告诉框架该怎么构建你的格子或者别的类。
一般来说返回一个匿名内部类就好了。看例子。

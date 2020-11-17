package com.tends.nioseeks.designmode.singles;

//单例模式、比如：工程类实例化采用此模式
public class TestSingleton {
    ////懒汉式单例、只有当第一次调用 getlnstance 时才创建这个单例
    ////若编写的是多线程程序，则不要删除代码中的关键字 volatile 和 synchronized，否则存在线程安全问题
    ////若不删除这两关键字能保证线程安全，但是每次访问时都要同步，会影响性能，消耗更多资源
    //private static volatile TestSingleton instance = null;    //保证 instance 在所有线程中同步
    //
    //private TestSingleton() { //禁止类在外部被实例化 }
    //
    //public static synchronized TestSingleton getInstance() {  //getInstance 方法前加同步
    //    if (instance == null)  instance = new TestSingleton();
    //    return instance;
    //}


    //饿汉式单例、 类一旦加载就创建一个单例，保证在调用 getInstance 之前单例就存在了
    //它在类创建的同时就已经创建好一个静态的对象供系统使用，以后不再改变，是线程安全的，能直接用于多线程
    private static final TestSingleton instance = new TestSingleton();

    private TestSingleton(){}

    public static TestSingleton getInstance() {
        return instance;
    }
}

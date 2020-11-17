package com.tends.nioseeks.designmode.factorys;

//原型模式的克隆分为浅克隆和深克隆。
//    浅克隆：创建一个新对象，新对象的属性和原来对象完全相同，对于非基本类型属性，仍指向原有属性所指向的对象的内存地址。
//    深克隆：创建一个新对象，属性中引用的其他对象也会被克隆，不再指向原有对象地址。
//Java中的Object类提供了浅克隆的clone()方法，原型类只要实现Cloneable接口就可实现对象的浅克隆，
public class TestRealizetype implements Cloneable {

    TestRealizetype() {
        System.out.println("具体原型创建成功！");
    }

    public Object clone() throws CloneNotSupportedException {
        System.out.println("具体原型复制成功！");
        return (TestRealizetype) super.clone();
    }


    //原型模式通常适用于以下场景: 
    //  对象之间相同或相似，即只是个别的几个属性不同的时候。
    //  创建对象成本较大，例如初始化时间长，占用CPU太多，或者占用网络资源太多等，需要优化资源。
    //  创建一个对象需要繁琐的数据准备或访问权限等，需要提高性能或者提高安全性。
    //  系统中大量使用该类对象，且各个调用者都需要给它的属性重新赋值。
    public static void main(String[] args) throws CloneNotSupportedException {
        TestRealizetype obj1 = new TestRealizetype();   //具体原型类
        TestRealizetype obj2 = (TestRealizetype) obj1.clone();
        System.out.println("obj1==obj2?" + (obj1 == obj2));
    }
}

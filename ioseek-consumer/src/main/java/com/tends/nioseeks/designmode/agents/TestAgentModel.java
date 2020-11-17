package com.tends.nioseeks.designmode.agents;

//代理模式（Proxy Pattern）:一个类代表另一个类的功能。这种类型的设计模式属于结构型模式。
//在代理模式中，我们创建具有现有对象的对象，为其他对象提供一种代理以控制对这个对象的访问。
public class TestAgentModel {
    //按职责来划分，通常有以下使用场景： 1、远程代理。 2、虚拟代理。 3、Copy-on-Write 代理。 4、保护（Protect or Access）代理。
    // 5、Cache代理。 6、防火墙（Firewall）代理。 7、同步化（Synchronization）代理。 8、智能引用（Smart Reference）代理
    // 1、和适配器模式的区别：适配器模式主要改变所考虑对象的接口，而代理模式不能改变所代理类的接口。
    // 2、和装饰器模式的区别：装饰器模式为了增强功能，而代理模式是为了加以控制


    interface Image {
        void display();
    }
    static class RealImage implements Image {
        private String fileName;

        public RealImage(String fileName){
            this.fileName = fileName;
            loadFromDisk(fileName);
        }
        @Override
        public void display() {
            System.out.println("Displaying " + fileName);
        }
        private void loadFromDisk(String fileName){
            System.out.println("Loading " + fileName);
        }
    }
    static class ProxyImage implements Image{
        private RealImage realImage;
        private String fileName;

        public ProxyImage(String fileName){
            this.fileName = fileName;
        }
        @Override
        public void display() {
            if(realImage == null){
                realImage = new RealImage(fileName);
            }
            realImage.display();
        }
    }





        public static void main(String[] args) {
            Image image = new ProxyImage("test_10mb.jpg");

            // 图像将从磁盘加载
            image.display();
            System.out.println("");
            // 图像不需要从磁盘加载
            image.display();
        }

}

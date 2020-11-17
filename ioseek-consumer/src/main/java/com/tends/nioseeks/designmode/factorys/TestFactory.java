package com.tends.nioseeks.designmode.factorys;

//工厂模式有3种不同的实现方式：简单工厂模式、工厂方法模式、抽象工厂模式
public class TestFactory {

    ////简单工厂模式、又叫静态工厂方法模式
    ////只要一个工厂类就可以完成所有目标对象，这种模式叫“简单工厂模式”
    //public static TestProduct simpleProduct(int kind) {
    //    return new TestProduct();
    //}
    //
    //private static class TestProduct {
    //    public void show() {
    //        System.out.println("具体产品显示...");
    //    }
    //}



    ////工厂方法模式、由抽象工厂、具体工厂、抽象产品和具体产品等4个要素构成
    //public static void main(String[] args) {
    //    try {
    //        TestProduct a;
    //        a = getObject(SubProduct1.class);
    //        System.out.println(a);
    //        a.show();
    //        a = getObject(SubProduct2.class);
    //        System.out.println(a);
    //        a.show();
    //    } catch (Exception e) {
    //        System.out.println(e.getMessage());
    //    }
    //}
    //public static <T> T getObject(Class<T> tClass) throws Exception{
    //    //Class alz = Class.forName(className);
    //    return tClass.newInstance();
    //}
    //
    ////抽象产品：提供了产品的接口
    //interface TestProduct {
    //    public void show();
    //}
    //static class SubProduct1 implements TestProduct {
    //    public void show() {
    //        System.out.println("产品1显示...");
    //    }
    //}
    //static class SubProduct2 implements TestProduct {
    //    public void show() {
    //        System.out.println("产品2显示...");
    //    }
    //}



    public static void main(String[] args) {
        //获取形状工厂
        AbstractFactory shapeFactory = FactoryProducer.getFactory("SHAPE");
        //获取形状为 Rectangle 的对象
        Shape shape2 = shapeFactory.getShape("RECTANGLE");
        //调用 Rectangle 的 draw 方法
        shape2.draw();
        //获取形状为 Square 的对象
        Shape shape3 = shapeFactory.getShape("SQUARE");
        //调用 Square 的 draw 方法
        shape3.draw();
    }
    //抽象工厂模式（Abstract Factory Pattern）：围绕一个超级工厂创建其他工厂。该超级工厂又称为其他工厂的工厂。
    //这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。
    //在抽象工厂模式中，接口是负责创建一个相关对象的工厂，不需要显式指定它们的类。每个生成的工厂都能按照工厂模式提供对象
    private interface Shape {
        void draw();
    }
    static class Rectangle implements Shape {
        @Override
        public void draw() {
            System.out.println("Inside Rectangle::draw() method.");
        }
    }
    static class Square implements Shape {
        @Override
        public void draw() {
            System.out.println("Inside Square::draw() method.");
        }
    }

    //abstract class AbstractFactory {
    interface AbstractFactory {
        public abstract Shape getShape(String shape) ;
        //public abstract Color getColor(String color);
    }
    static class FactoryProducer {
        public static AbstractFactory getFactory(String choice){
            if(choice.equalsIgnoreCase("SHAPE")){
                return new ShapeFactory();
            } else if(choice.equalsIgnoreCase("COLOR")){
                //return new ColorFactory();
            }
            return null;
        }
    }
    static class ShapeFactory implements AbstractFactory {
        @Override
        public Shape getShape(String shapeType){
            if(shapeType == null)  return null;
            if(shapeType.equalsIgnoreCase("RECTANGLE")){
                return new Rectangle();
            } else if(shapeType.equalsIgnoreCase("SQUARE")){
                return new Square();
            }
            return null;
        }
    }
















    
}

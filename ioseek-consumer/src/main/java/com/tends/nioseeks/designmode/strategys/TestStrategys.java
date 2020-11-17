package com.tends.nioseeks.designmode.strategys;

//策略模式（Strategy Pattern）:一个类的行为或其算法可以在运行时更改。这种类型的设计模式属于行为型模式。
//策略模式：创建表示各种策略的对象和一个行为随着策略对象改变而改变的 context 对象。策略对象改变context对象的执行算法
public class TestStrategys {
    //定义一系列算法,把它们一个个封装起来并使它们可相互替换、若一个系统的策略多于四个，就需考虑使用混合模式解决策略类膨胀问题
    //使用场景：1、如一个系统里面许多类它们之间的区别仅在于它们的行为，那么用策略模式可以动态的让一个对象在许多行为中选择一种行为
    //2、一个系统需要动态地在几种算法中选择一种。 3、若一个对象有很多的行为，如不用恰当的模式，这些行为只好用多重的条件选择语句来实现


    interface Strategy {
        public int doOperation(int num1, int num2);
    }
    
    static class OperationAdd implements Strategy{
        @Override
        public int doOperation(int num1, int num2) {
            return num1 + num2;
        }
    }
    static class OperationSubtract implements Strategy{
        @Override
        public int doOperation(int num1, int num2) {
            return num1 - num2;
        }
    }
    static class OperationMultiply implements Strategy{
        @Override
        public int doOperation(int num1, int num2) {
            return num1 * num2;
        }
    }

    static class Context {
        private Strategy strategy;

        public Context(Strategy strategy){
            this.strategy = strategy;
        }
        public int executeStrategy(int num1, int num2){
            return strategy.doOperation(num1, num2);
        }
    }





    public static void main(String[] args) {
        Context context = new Context(new OperationAdd());
        System.out.println("10 + 5 = " + context.executeStrategy(10, 5));

        context = new Context(new OperationSubtract());
        System.out.println("10 - 5 = " + context.executeStrategy(10, 5));

        context = new Context(new OperationMultiply());
        System.out.println("10 * 5 = " + context.executeStrategy(10, 5));
    }
    
}

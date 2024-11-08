## 单例模式



## 代理模式

### 静态代理

#### 实现步骤：

1. 创建目标接口
2. 创建目标接口实现类
3. 创建目标接口代理类，并将实现类以private形式封装到代理类中，以实现封闭

### 动态代理

java中的动态代理有两种

1. `JDK`自带
   - 缺点：不能代理没有实现接口的类，否则抛类型转换异常。原因来自于`Proxy.newInstance()`方法
2. `CGLIB`
   - 通过继承方式实现代理

#### JDK实现步骤

1. 定义一个接口及其实现类
2. 实现`InvocationHandler`接口，并重写`invoke()`方法
3. 通过`Proxy.newInstance(ClassLoader, Interface, InvocationHandler)`来创建代理类

### 静态代理和动态代理的区别

#### 静态代理

**优点**：

- 实现简单，提前定义好代理类，结构清晰，代理行为固定。
- 编译期生成，执行速度快，性能较好。

**缺点**：

- 代理类多，若接口有变动，需修改每个代理类，维护成本高。
- 不适合扩展或动态代理，灵活性差。

#### 动态代理

**优点**：

- 运行时生成代理对象，减少代码冗余，提升扩展性和灵活性。
- 适合处理多个接口或功能切面，常用于AOP（如日志、事务等）。

**缺点**：

- 运行期生成，性能稍差，调试困难。

## 语法糖

### 类擦除

#### 类型擦除的基本概念

在 Java 中，泛型是在编译时引入的，编译器会根据泛型参数的类型进行检查，但在运行时，所有的泛型信息都会被擦除，转变为原始类型。这意味着，在运行时，泛型的具体类型不再可用，所有泛型类和接口的实例都将被转换为其原始类型。

#### 类型擦除的过程

##### 1.原始类型替换

在泛型类和接口的实现中，所有的泛型参数都被替换为它们的原始类型。例如，`List<T>` 会被替换为 `List`，`Map<K, V>` 会被替换为 `Map`。这是类型擦除的基本步骤。

##### 2.边界类型的处理

如果泛型参数有边界限制，比如 `T extends Number`，在擦除过程中，编译器会将其替换为边界类型的原始类型。例如，`List<T extends Number>` 会被替换为 `List<Number>`。如果没有边界限制，则会替换为 `Object`。

```java
public class Box<T> {
    private T value;
}
```

上述代码在类型擦除后，会变为：

```java
public class Box {
    private Object value; // T 被替换为 Object
}
```

##### 3.类型检查和强制转换

编译器会在泛型使用的位置添加强制类型转换，以确保类型安全。例如，在泛型方法中，编译器会检查类型并添加相应的强制转换。

```java
public <T> void add(T element) {
    list.add(element); // 在运行时会添加强制转换
}
```

擦除后可能变为：

```java
public void add(Object element) {
    list.add(element); // 需要保证 list 的元素类型是正确的
}
```

#### 类型擦除的影响

##### 1.限制

- **不能使用 `instanceof `检查泛型类型**：由于泛型信息在运行时不可用，不能使用 `instanceof` 操作符来检查泛型类型。

```java
if (element instanceof T) { // 这会导致编译错误
    // ...
}
```

- **不能创建泛型数组**：因为类型在运行时不可用，所以不能创建泛型数组。

```java
T[] array = new T[10]; // 编译错误
```

##### 2.反映在字节码中

使用 `javap` 工具可以查看编译后生成的字节码，验证类型擦除的过程。例如，对于一个简单的泛型类：

```java
public class Box<T> {
    private T value;
    
    public void setValue(T value) {
        this.value = value;
    }
}
```

生成的字节码中会看到 `T` 被替换为 `Object`。

#### 总结

- Java 的类型擦除机制是为了兼容性和向后兼容性，使得泛型能够与非泛型的代码共存。
- 在编译期间，泛型参数会被替换为其原始类型，而所有与泛型相关的类型信息在运行时不可用。
- 类型擦除提供了类型安全性，但也带来了一些限制，如无法使用 `instanceof` 检查泛型类型和创建泛型数组。

### `Enum`

<img src="D:\测试开发\笔记\Java基础\pic\1.png" style="zoom: 80%;" />

`enum`类型本质上是语法糖，我们来看看反编译的结果：

```java
public enum t {
    SPRING,SUMMER;
}
```

```java
public final class T extends Enum
{
    private T(String s, int i)
    {
        super(s, i);
    }
    public static T[] values()
    {
        T at[];
        int i;
        T at1[];
        System.arraycopy(at = ENUM$VALUES, 0, at1 = new T[i = at.length], 0, i);
        return at1;
    }

    public static T valueOf(String s)
    {
        return (T)Enum.valueOf(demo/T, s);
    }

    public static final T SPRING;
    public static final T SUMMER;
    private static final T ENUM$VALUES[];
    static
    {
        SPRING = new T("SPRING", 0);
        SUMMER = new T("SUMMER", 1);
        ENUM$VALUES = (new T[] {
            SPRING, SUMMER
        });
    }
}
```

我们重点来看看`valueOf`方法的源码

```java
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        T result = (Enum)enumClass.enumConstantDirectory().get(name);
        if (result != null) {
            return result;
        } else if (name == null) {
            throw new NullPointerException("Name is null");
        } else {
            throw new IllegalArgumentException("No enum constant " + enumClass.getCanonicalName() + "." + name);
        }
    }
```

## Object类
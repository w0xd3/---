## JUC

### Java线程状态

![](.\pic\1.png)

### 死锁

#### 产生条件

1. **互斥条件（Mutual Exclusion）**
   资源是独占的，即一次只能被一个线程占有。如果某个线程占有资源，其他线程只能等待，不能同时访问该资源。
   **示例**：假设线程A占用了资源X，线程B占用了资源Y，线程A和线程B无法同时访问对方的资源。
2. **占有并等待条件（Hold and Wait）**
   一个线程已占有至少一个资源，但它还在等待其他线程占用的资源，同时不释放自己已占有的资源。
   **示例**：线程A已经持有资源X，同时等待资源Y；线程B已经持有资源Y，同时等待资源X。
3. **不可剥夺条件（No Preemption）**
   已经获得的资源在未使用完之前不能被强制剥夺，只有资源占有的线程主动释放，资源才能被其他线程使用。
   **示例**：线程A持有资源X，而线程B无法强制让线程A释放资源X，只能等待线程A主动释放该资源。
4. **循环等待条件（Circular Wait）**
   存在一个线程等待链，如线程A等待线程B持有的资源，线程B等待线程C持有的资源，线程C又等待线程A持有的资源，形成一个循环等待的链。

#### 防止死锁

1. **破坏互斥条件**：如果可以的话，让资源变为共享资源（非独占）。
2. **破坏占有并等待条件**：要求线程一次性申请所需的所有资源。
3. **破坏不可剥夺条件**：允许抢占资源，强制释放已经占用的资源。
4. **破坏循环等待条件**：通过按顺序申请资源，避免循环等待。

### `Volatile` 关键字

#### 主要特性

1. **可见性**
   - 当一个线程修改了被 `volatile` 修饰的变量的值，其他线程能够立即看到这个修改。这是因为 `volatile` 变量不在 CPU 的缓存中，而是直接从**主内存**中读取。
2. **有序性**
   - `volatile` 变量在某种程度上可以**防止指令重排序**。访问 `volatile` 变量的读操作和写操作不会被重排序。

#### 使用场景

##### 单例模式-懒加载-双重锁定检查

```java
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

```

#### 注意事项

1. **不保证原子性**
   - `volatile` 不提供原子性。如果需要保证操作的原子性，仍然需要使用 `synchronized` 或 `Lock`。
2. **限制**
   - `volatile` 仅适用于简单的读写操作，不能用于复合操作（例如，`i++`），因为这些操作不是原子的。
3. **性能**
   - 在多线程环境中，使用 `volatile` 可以减少由于加锁而产生的开销，但在频繁写入的场景中，它可能会影响性能，因为每次写入都会同步到主内存。

### `synchronized` 关键字

`synchronized` 是 Java 中用于实现线程安全的一种机制。它可以用来修饰方法或代码块，以确保在同一时刻只有一个线程能够执行被修饰的部分，从而避免线程之间的冲突和数据不一致。

#### 主要特点

1. **互斥性**：
   - 当一个线程获得了 `synchronized` 修饰的锁时，其他线程必须等待，直到锁被释放。这样可以防止多个线程同时访问同一资源，造成数据竞争。
2. **可重入性**：
   - `synchronized` 是可重入的，意味着同一线程可以多次获得同一把锁而不会导致死锁。这在递归调用或方法间调用时非常有用。
3. **锁的粒度**：
   - `synchronized` 可以应用于整个方法（实例方法或静态方法）或代码块（具体的对象或类）。选择适当的锁粒度可以提高性能。

#### 使用方式

- **修饰实例方法**：

  ```java
  public synchronized void instanceMethod() {
      // 只有一个线程可以访问这个方法
  }
  ```

- **修饰静态方法**：

  ```java
  public static synchronized void staticMethod() {
      // 只有一个线程可以访问这个方法
  }
  ```

- **修饰代码块**：

  ```java
  public void method() {
      synchronized (this) {
          // 只有持有该对象锁的线程才能执行
      }
  }
  ```

#### 内部机制

- **每个对象都有一个锁**：每个 Java 对象都有一个监视器锁，使用 `synchronized` 时会获取这个锁。
- **JVM 的调度**：当一个线程尝试获取一个已经被其他线程占用的锁时，它将被阻塞，直到锁被释放。

### 示例代码

```java
public class SynchronizedExample {
    private int counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public synchronized int getCounter() {
        return counter;
    }

    public static void main(String[] args) {
        SynchronizedExample example = new SynchronizedExample();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                example.increment();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                example.increment();
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final counter value: " + example.getCounter());
    }
}
```

#### 优点：

- 简单易用，代码可读性好。
- 确保线程安全，避免数据不一致。

#### 缺点：

- 性能开销：获取和释放锁的开销会影响性能，特别是在高并发场景中。
- 可能导致线程阻塞，降低系统响应速度。

### 悲观锁和乐观锁

#### **悲观锁（Pessimistic Lock）**

悲观锁假设数据的并发冲突是非常常见的，因此在对数据进行任何修改之前，必须先锁住数据，确保其他事务无法对该数据进行操作，直到当前事务完成。

- **工作原理**：在读取或修改数据时，悲观锁会立即对数据进行加锁，其他事务必须等待，直到锁被释放后才能继续操作。这通常通过数据库的锁机制来实现，例如表锁或行锁。
- **适用场景**：悲观锁适用于数据冲突频繁的场景。例如在高并发环境中，若数据修改的竞争较大，使用悲观锁可以确保数据的一致性。
- **缺点**：由于悲观锁会阻塞其他事务，可能会导致性能下降，尤其是在锁持有时间长或并发量大的情况下。

**示例**：

```sql
SELECT * FROM table WHERE id = 1 FOR UPDATE;
```

#### **乐观锁（Optimistic Lock）**

乐观锁假设数据冲突不常发生，因此不会对数据进行加锁，而是通过数据版本或时间戳等机制来检测是否发生了并发冲突。只有在提交数据时，才检查数据是否被其他事务修改过。

- **工作原理**：乐观锁通常依赖一个版本号或时间戳。在每次修改数据时，会检查该版本号或时间戳是否与数据库中一致，如果不一致，说明有其他事务修改过此数据，当前事务需要重新获取数据再执行。
- **适用场景**：适用于数据冲突较少的场景。乐观锁避免了加锁带来的性能开销，适合读取多、写入少的场景，例如电商中的商品库存管理。
- **缺点**：在高并发环境下，冲突频繁时可能导致事务多次重试，降低了系统性能。

**示例**：

```sql
UPDATE table 
SET value = 'newValue', version = version + 1 
WHERE id = 1 AND version = 2;
```

#### 区别：

- **锁机制**：悲观锁依赖于数据库的物理锁，乐观锁依赖版本号或时间戳来进行校验。
- **并发控制**：悲观锁在读取时就会加锁，阻止其他事务，乐观锁只有在提交时才检测是否有冲突。
- **性能影响**：悲观锁会导致事务阻塞，影响性能；乐观锁没有锁的开销，但可能因为冲突重试影响效率。

总结来说，**悲观锁**适合高冲突、频繁写操作的场景，**乐观锁**更适合低冲突、频繁读操作的场景。

### CAS(乐观锁的实现之一)

CAS（Compare and Swap，比较并交换）是一种实现**无锁并发**的操作，广泛应用于多线程编程中。它通过比较和交换操作来确保数据的原子性，避免了使用锁机制，从而提高了并发性能。

#### **CAS 的工作原理**

CAS 操作包含三个操作数：

- **内存地址（V）**：需要操作的变量的内存地址。
- **预期值（A）**：期望的值。
- **新值（N）**：要写入的新值。

#### **CAS 的应用场景**

CAS 被广泛用于实现**无锁算法**，在高并发环境下具有极高的性能优势。常见的应用包括：

- **原子操作类**：Java 的 `java.util.concurrent.atomic` 包中的类（如 `AtomicInteger`、`AtomicReference`）都是通过 CAS 实现的原子操作。
- **并发队列**：像 `ConcurrentLinkedQueue` 这样的无锁队列，也通过 CAS 来确保线程安全。
- **自旋锁**：CAS 常用于实现自旋锁，在等待锁的过程中不断尝试通过比较和交换来获取锁，而不是像普通锁那样阻塞线程。

####  **CAS 的优势**

- **高性能**：由于 CAS 操作不需要使用锁，因此避免了线程的上下文切换和死锁等问题。在读多写少的场景下，CAS 提供了比锁机制更好的性能。
- **无锁并发**：在多线程竞争不激烈时，CAS 可以避免锁带来的性能损耗，尤其适合轻量级的原子操作。

#### **CAS 的缺点**

尽管 CAS 非常高效，但也存在一些局限性：

1. **ABA问题**：CAS 操作中，如果一个变量从值 A 变成 B，之后又变回 A，CAS 操作会认为没有变化，导致程序误判。这种情况被称为 **ABA问题**。Java 的 `AtomicStampedReference` 类可以通过版本号来解决这个问题，即每次修改时不仅改变值，还增加版本号，确保变化被检测到。
2. **自旋开销**：如果 CAS 操作失败，通常会进入自旋重试，即不断进行比较和交换。这在高并发场景下可能会导致 CPU 资源的浪费。
3. **只能保证一个变量的原子性**：CAS 只能操作一个变量，无法处理多个变量的原子性问题。如果需要同时更新多个变量，就需要借助锁机制。

####  **CAS 在 Java 中的使用**

CAS 是 Java 中 `AtomicInteger`、`AtomicLong`、`AtomicReference` 等类的底层实现原理。通过这些类可以实现高效的原子操作，而不必使用同步锁。

**示例**：`AtomicInteger` 类的 `compareAndSet()` 方法就是基于 CAS 实现的：

```java
public class CASExample {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        
        boolean success = atomicInteger.compareAndSet(5, 10); // 如果当前值是5，则将其更新为10
        System.out.println("Update success: " + success);  // true
        
        System.out.println("Current value: " + atomicInteger.get());  // 10
    }
}
```

### ReentrantLock 

#### 与 `synchronized` 的区别

- **锁的获取方式**：`ReentrantLock` 提供了显式的锁操作，需要调用 `lock()` 和 `unlock()` 方法来获取和释放锁，而 `synchronized` 是隐式的，进入同步代码块时自动获取锁，退出时自动释放锁。
- **锁的释放**：`synchronized` 自动释放锁，而 `ReentrantLock` 需要在 `finally` 中显式地调用 `unlock()` 方法释放锁，避免死锁。
- **可中断锁**：`ReentrantLock` 提供了 `lockInterruptibly()` 方法，允许线程在等待锁时可以响应中断，而 `synchronized` 则无法做到。
- **公平锁和非公平锁**：`ReentrantLock` 提供了公平锁的支持，通过构造函数可以指定是否为公平锁（默认是非公平锁）。公平锁是指线程将按照先来先服务的顺序获取锁，非公平锁则可能会插队。

#### ReentrantLock 的常用方法

- **lock()**：获取锁，如果锁已经被其他线程持有，则当前线程进入阻塞状态。
- **lockInterruptibly()**：获取锁，但在等待的过程中可以被中断。
- **tryLock()**：尝试获取锁，成功返回 `true`，失败返回 `false`，不会阻塞线程。
- **tryLock(long timeout, TimeUnit unit)**：尝试在指定的时间内获取锁，如果在超时前获取到锁则返回 `true`，否则返回 `false`。
- **unlock()**：释放锁。
- **newCondition()**：获取 `Condition` 对象，用于线程间的协调。

### Unsafe

`Unsafe` 类是 Java 中提供的一种底层工具类，它位于 `sun.misc` 包中，用于执行一些普通 Java 程序无法访问的底层操作，比如：

- 直接操作内存
- 线程调度
- CAS（Compare-And-Swap）操作
- 获取对象的字段偏移量
- 操作对象字段
- 线程挂起和恢复

[Java 魔法类 Unsafe 详解 | JavaGuide](https://javaguide.cn/java/basis/unsafe.html#线程调度)
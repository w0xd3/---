#### webdriver源码解析

阅读`find_element()`源码发现，无论是传入哪种形式，底层都是调用`CSS_SELECTOR`的，GPT说这是官方做的优化，速度是最快的。同时webdriver底层大量调用`execute()`方法，这种设计模式叫命**令模式**。在命令模式中，操作被封装为对象，从而使你可以将请求参数化、延迟执行和支持操作撤销等。

- **get(url)**: 打开指定的网页。
- **find_element(by, value)**: 查找单个元素，返回一个元素对象。
- **find_elements(by, value)**: 查找多个元素，返回元素列表。
- **click()**: 点击元素。
- **send_keys(\*value)**: 向输入框发送文本。
- **execute_script(script, \*args)**: 执行 JavaScript 代码。
- **quit()**: 关闭浏览器并结束会话。
- **get_cookies()**: 获取当前会话的所有 cookies。
- **add_cookie(cookie_dict)**: 添加一个 cookie。
- **switch_to.window(window_name)**: 切换到指定的窗口或标签页。

#### selenium的显示等待和隐式等待

#### 显示等待

- **定义**：显示等待是一种在运行时动态等待特定条件发生的机制。你可以为某个特定的元素设置最长等待时间，直到条件成立（例如元素可见、可点击等）。

- **实现方式**：使用`WebDriverWait`类结合`expected_conditions`模块来指定等待条件。

- 示例：

  ```python
  from selenium.webdriver.common.by import By
  from selenium.webdriver.support.ui import WebDriverWait
  from selenium.webdriver.support import expected_conditions as EC
  
  wait = WebDriverWait(driver, 10)  # 最长等待10秒
  element = wait.until(EC.visibility_of_element_located((By.ID, 'element_id')))
  ```

#### 隐式等待

- **定义**：隐式等待是在WebDriver实例上全局设置的等待时间。当查找元素时，WebDriver会在指定的时间内轮询DOM以查找元素。如果在这个时间内找不到元素，将抛出异常。

- **实现方式**：使用`driver.implicitly_wait(time)`来设置等待时间。

- 示例：

  ```python
  driver.implicitly_wait(10)  # 设置隐式等待时间为10秒
  element = driver.find_element(By.ID, 'element_id')
  ```

####  **区别总结**

当一个元素在 Selenium 中配置了**显式等待**时，**隐式等待**不会对这个操作生效。Selenium 优先执行显式等待的逻辑，而忽略隐式等待的默认超时时间。

| 特性     | 隐式等待                     | 显示等待                   |
| -------- | ---------------------------- | -------------------------- |
| 等待方式 | 统一的固定时间               | 针对特定条件设定           |
| 适用范围 | WebDriver 的所有元素查找操作 | 单次查找，可灵活调整条件   |
| 适用场景 | 简单页面加载延迟             | 特定事件、动态元素的等待   |
| 推荐场景 | 稳定页面，元素加载无明显延迟 | 动态页面，有特定条件需等待 |

#### Selenium工作原理

**架构组件**

- **Selenium WebDriver**: 提供一个编程接口，允许开发者用代码控制浏览器。WebDriver 是与浏览器进行交互的核心组件。
- **浏览器驱动**: 每种浏览器都有特定的驱动程序（如 ChromeDriver、GeckoDriver 等），用于与浏览器进行通信。
- **浏览器**: 实际执行用户界面的软件，如 Chrome、Firefox 等。

**工作流程**

1. **编写测试脚本**: 开发者使用 Selenium 提供的 API 编写测试脚本，指定要执行的操作，如打开网页、点击按钮、填写表单等。

   ```python
   from selenium import webdriver
   
   # 创建 WebDriver 实例
   driver = webdriver.Chrome()
   
   # 打开一个网页
   driver.get("http://example.com")
   
   # 找到元素并进行操作
   button = driver.find_element_by_id("submit-button")
   button.click()
   ```

2. **发送请求**: Selenium WebDriver 将操作转换为 HTTP 请求，并发送到相应的浏览器驱动。请求包括要执行的操作、要访问的 URL 等。

3. **浏览器驱动处理请求**: 浏览器驱动接收请求，解析并将其转发到实际的浏览器实例。驱动是与浏览器通信的桥梁，负责将 WebDriver 的命令转化为浏览器可以理解的格式。

4. **执行操作**: 浏览器接收到指令后，执行相应的操作（如加载网页、单击按钮等），并根据操作结果返回状态信息。

5. **返回结果**: 浏览器驱动将执行结果（如当前页面的 URL、元素的状态、截图等）返回给 Selenium WebDriver。

6. **结束测试**: 测试脚本可以继续执行或结束。开发者可以通过调用 `driver.quit()` 来关闭浏览器和释放资源。

**案例：**利用`Insomia`直接调用`Chorme WebDriver`实现对浏览器元素的操纵

### `Click()`失效的原因

1. **Selenium 的 Click 方法与 JavaScript 点击行为不兼容**

   - 在一些页面上，尤其是使用大量 JavaScript 控制交互的页面，Selenium 的原生 `click()` 方法可能无法触发 JavaScript 绑定的事件（如 `onclick` 或 `onchange`），导致点击失效。

   - **解决方案**：使用 JavaScript 模拟点击。`execute_script` 会直接在浏览器中执行 JavaScript，绕过 Selenium 的原生点击。

   - ```python
     element = driver.find_element(By.ID, 'element_id')
     driver.execute_script("arguments[0].click();", element)
     ```

2. **浏览器驱动和浏览器版本不兼容**

3. **元素位置不稳定**

   - 如果元素的位置在点击时不稳定（可能页面还在加载、元素在调整位置等），会导致 `click()` 不生效。
   - **解决方案**：利用显式等待

4. **点击元素被阻挡或被覆盖**

   - Selenium 的 `click()` 方法要求元素完全可见，如果页面上有遮挡元素（如弹窗、广告等），则点击会失效。
   - **解决方案**：可以滚动到元素位置，或等待遮挡元素消失再点击。

### `ActionChains`
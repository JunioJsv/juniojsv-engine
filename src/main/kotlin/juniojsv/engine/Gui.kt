package juniojsv.engine

import javax.swing.*
import javax.swing.event.ListDataListener

class Gui(engine: Engine) : Runnable {
    val console = Console(engine)
    private val editor = Editor(engine)

    override fun run() {
        JFrame(Thread.currentThread().name).apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            layout = BoxLayout(contentPane, BoxLayout.X_AXIS)
            isResizable = false
            add(console)
            add(editor)
            pack()
            isVisible = true
        }
    }
}

class Console(engine: Engine) : JPanel() {
    private val output = JTextArea(20, 50).apply {
        lineWrap = true
        isEditable = false
    }
    private val input: JTextField = JTextField()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JScrollPane(output))
        add(
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(input)
                add(
                    JButton("Enter").apply {
                        addActionListener { event ->
                            when (event.actionCommand) {
                                "Enter" -> {
                                    input.text.split(" ").also { args ->
                                        engine.channel(
                                            args[0],
                                            args.filterIndexed { index, _ -> index != 0 }
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        )
    }

    fun print(msg: String) = output.append(msg)
    fun println(msg: String) = print("$msg\n")
    fun debug(
        title: String,
        source: String,
        type: String,
        severity: String,
        message: String
    ): String = """[$source] $title
                        Source: $source
                        Type: $type
                        Severity: $severity
                        Message: $message"""
}

class Editor(engine: Engine) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(
            JScrollPane(
                JList<Being>(object : ListModel<Being> {
                    override fun getElementAt(index: Int): Being =
                        (engine.channel("get_beings", null) as List<*>)[index] as Being

                    override fun getSize(): Int =
                        (engine.channel("get_beings", null) as List<*>).size

                    override fun addListDataListener(p0: ListDataListener?) = Unit

                    override fun removeListDataListener(p0: ListDataListener?) = Unit
                })
            )
        )
    }
}
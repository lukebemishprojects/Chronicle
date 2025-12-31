import dev.lukebemish.chronicle.core.ChronicleEngine
import test.generated.output.test.generator.target.RootImpl

fun main() {
    val engine = ChronicleEngine(RootImpl::class.java)
    val output = engine.execute {
        foo {
            bar {
                gizmo {
                    this["key"] = "value"
                }
            }
        }
    }
    println(output)
}

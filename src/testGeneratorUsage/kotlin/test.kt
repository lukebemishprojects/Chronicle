import dev.lukebemish.chronicle.core.ChronicleEngine
import test.generated.dev.lukebemish.chronicle.fabric.FabricModJsonImpl

fun main() {
    val engine = ChronicleEngine(FabricModJsonImpl::class.java)
    val output = engine.execute {
        id = "some-id"
        version = "1.2.3"
        depends {
            mod("test") {
                foo("1.0.0")
            }
        }
    }
    println(output)
}

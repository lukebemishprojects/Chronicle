package test.groovy

import dev.lukebemish.chronicle.core.ChronicleEngine
import test.generated.output.test.generator.target.RootImpl

class Test {
    static void main(String[] args) {
        def engine = new ChronicleEngine<>(RootImpl)
        println engine.execute {
            foo {
                bar {
                    gizmo {
                        key = "value"
                    }
                }
            }
        }
    }
}

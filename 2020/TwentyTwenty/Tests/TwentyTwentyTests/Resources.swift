
import Foundation

func loadTxtFile(named name: String) -> String {
    let inputUrl = Bundle.module.url(forResource: name, withExtension: "txt")!
    return try! String(contentsOf: inputUrl)
}

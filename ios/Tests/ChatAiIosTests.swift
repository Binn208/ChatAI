import XCTest
@testable import ChatAiIos

final class ChatAiIosTests: XCTestCase {

    override func setUp() {
        super.setUp()
        UserMemoryManager.shared.clearAll()
    }

    func testUserMemoryLearning() {
        let mem = UserMemoryManager.shared
        
        // 1. Học tên
        let resName = mem.analyzeAndLearn(prompt: "tôi tên là bin")
        XCTAssertNotNil(resName)
        XCTAssertEqual(mem.name, "bin")

        // 2. Học tuổi
        let resAge = mem.analyzeAndLearn(prompt: "mình 22 tuổi")
        XCTAssertNotNil(resAge)
        XCTAssertEqual(mem.age, "22 tuổi")

        // 3. Học nơi ở
        let resLoc = mem.analyzeAndLearn(prompt: "tôi sống ở hà nội")
        XCTAssertNotNil(resLoc)
        XCTAssertEqual(mem.location, "hà nội")

        // 4. Học nghề nghiệp
        let resJob = mem.analyzeAndLearn(prompt: "tôi làm lập trình viên ios")
        XCTAssertNotNil(resJob)
        XCTAssertEqual(mem.job, "lập trình viên ios")

        // 5. Học sở thích
        let resHobby = mem.analyzeAndLearn(prompt: "tôi thích viết code swiftui")
        XCTAssertNotNil(resHobby)
        XCTAssertEqual(mem.hobby, "viết code swiftui")

        XCTAssertTrue(mem.hasAnyMemory)
    }

    func testMockAiMemoryRecall() {
        let mem = UserMemoryManager.shared
        _ = mem.analyzeAndLearn(prompt: "tôi tên là bin")
        _ = mem.analyzeAndLearn(prompt: "tôi 20 tuổi")

        let replyName = MockAiEngine.shared.generateResponse(prompt: "tôi tên gì")
        XCTAssertTrue(replyName.contains("bin"))

        let replyAge = MockAiEngine.shared.generateResponse(prompt: "tôi bao nhiêu tuổi")
        XCTAssertTrue(replyAge.contains("20 tuổi"))
    }

    func testCodeFormatter() {
        let text = """
        Xin chào, đây là code:
        ```swift
        print("Hello iOS")
        ```
        Hết code.
        """

        let segments = CodeFormatterUtil.parseSegments(from: text)
        XCTAssertEqual(segments.count, 3)

        if case .code(_, let lang, let code) = segments[1] {
            XCTAssertEqual(lang, "Swift")
            XCTAssertTrue(code.contains("print(\"Hello iOS\")"))
        } else {
            XCTFail("Segment 1 must be code block")
        }
    }

    func testModelCatalogIntegrity() {
        let models = ModelCatalog.getRecommendedModels()
        XCTAssertEqual(models.count, 4)
        XCTAssertEqual(models[0].id, "qwen2.5-coder-0.5b")
        XCTAssertEqual(models[1].id, "qwen2.5-0.5b-instruct")
        XCTAssertEqual(models[2].id, "llama-3.2-1b-instruct")
        XCTAssertEqual(models[3].id, "gemma-2b-it")
    }
}

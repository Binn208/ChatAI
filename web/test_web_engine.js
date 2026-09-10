const fs = require('fs');
const path = require('path');

// Extract the MockAiEngine & UserMemoryManager from app.js to test in Node environment
const appJs = fs.readFileSync(path.join(__dirname, 'app.js'), 'utf8');

// Mock localStorage
const store = {};
global.localStorage = {
    getItem: (k) => store[k] || null,
    setItem: (k, v) => { store[k] = v.toString(); },
    removeItem: (k) => { delete store[k]; }
};

const vm = require('vm');

// Evaluate the UserMemoryManager and MockAiEngine classes in sandbox
vm.runInThisContext(appJs.slice(0, appJs.indexOf('document.addEventListener')));

console.log("==========================================");
console.log("   RUNNING WEB CHAT AI ENGINE UNIT TESTS  ");
console.log("==========================================");

let passed = 0;
let failed = 0;

function assert(desc, condition) {
    if (condition) {
        console.log(`[PASS] ${desc}`);
        passed++;
    } else {
        console.error(`[FAIL] ${desc}`);
        failed++;
    }
}

const memory = new UserMemoryManager();

// Test 1: Math
const mathRes = MockAiEngine.generateResponse("15 * 8 = ?", [], memory);
assert("Math 15 * 8 returns 120", mathRes.includes("120"));

// Test 2: Sqrt
const sqrtRes = MockAiEngine.generateResponse("căn 144", [], memory);
assert("Sqrt 144 returns 12", sqrtRes.includes("12"));

// Test 3: Appetize
const appetizeRes = MockAiEngine.generateResponse("Appetize là gì?", [], memory);
assert("Appetize question returns Appetize.io info", appetizeRes.includes("Appetize.io"));

// Test 4: OOP
const oopRes = MockAiEngine.generateResponse("OOP có những tính chất gì?", [], memory);
assert("OOP question returns 4 pillars", oopRes.includes("Đóng gói") && oopRes.includes("Kế thừa"));

// Test 5: Learn name
const learnRes = MockAiEngine.generateResponse("Tôi tên là Bin, năm nay 21 tuổi, sống ở Hà Nội", [], memory);
assert("Learn memory confirms name & age & location", memory.memory.name === "Bin" && memory.memory.age.includes("21") && memory.memory.location.includes("Hà Nội"));

// Test 6: Query memory
const queryRes = MockAiEngine.generateResponse("Bạn nhớ gì về tôi?", [], memory);
assert("Query memory returns profile summary", queryRes.includes("Bin") && queryRes.includes("21 tuổi") && queryRes.includes("Hà Nội"));

// Test 7: Name query
const nameRes = MockAiEngine.generateResponse("Tôi tên là gì?", [], memory);
assert("Name query returns Bin", nameRes.includes("Bin"));

console.log("==========================================");
console.log(`WEB ENGINE TEST RESULT: ${passed} PASS, ${failed} FAIL`);
console.log("==========================================");

if (failed > 0) process.exit(1);

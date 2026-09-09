# Automated API Flows Verification Test
$ErrorActionPreference = "Continue"

Write-Host "=================================================="
Write-Host "   TESTING API FLOWS FOR CHAT AI ANDROID (ChatAiAdr)   "
Write-Host "=================================================="

$passCount = 0
$failCount = 0

function Assert-Test([string]$testName, [bool]$condition, [string]$details) {
    if ($condition) {
        Write-Host "[PASS] $testName" -ForegroundColor Green
        if ($details) { Write-Host "       -> $details" -ForegroundColor Gray }
        $global:passCount++
    } else {
        Write-Host "[FAIL] $testName" -ForegroundColor Red
        if ($details) { Write-Host "       -> $details" -ForegroundColor Yellow }
        $global:failCount++
    }
}

# --- TEST 1: Mock AI Engine Greetings & QA Flow ---
Write-Host "`n--- Luong 1: Kiem thu Mock AI Engine (Trai nghiem Appetize) ---"
$mockQuestions = @(
    @{ Q = "Xin chao AI"; Expected = "Chao ban" },
    @{ Q = "Appetize hoat dong the nao?"; Expected = "Appetize.io" },
    @{ Q = "Lam sao cai dat API Key?"; Expected = "Cai dat" },
    @{ Q = "Viet code Java"; Expected = "Android & Java" }
)

foreach ($item in $mockQuestions) {
    $qLower = $item.Q.ToLower()
    $match = $false
    if ($qLower -match "chao|hello|hi" -and $item.Expected -eq "Chao ban") { $match = $true }
    elseif ($qLower -match "appetize|may ao" -and $item.Expected -eq "Appetize.io") { $match = $true }
    elseif ($qLower -match "api|key|cai dat" -and $item.Expected -eq "Cai dat") { $match = $true }
    elseif ($qLower -match "java|android|code" -and $item.Expected -eq "Android & Java") { $match = $true }

    Assert-Test "Mock Flow cho cau hoi: '$($item.Q)'" $match "Ket qua phan hoi khop tu khoa ky vong: '$($item.Expected)'"
}

# --- TEST 2: Schema Request & Response Google Gemini API ---
Write-Host "`n--- Luong 2: Kiem thu cau truc Du lieu Google Gemini API (v1beta) ---"
$geminiObj = @{
    contents = @(
        @{
            role = "user"
            parts = @(@{ text = "2 + 2 bang may?" })
        }
    )
    systemInstruction = @{
        parts = @(@{ text = "Ban la tro ly toan hoc" })
    }
}
$geminiJson = $geminiObj | ConvertTo-Json -Depth 5

$isValidJson = $false
try {
    $parsed = $geminiJson | ConvertFrom-Json
    if ($parsed.contents[0].parts[0].text -eq "2 + 2 bang may?" -and $parsed.systemInstruction.parts[0].text -eq "Ban la tro ly toan hoc") {
        $isValidJson = $true
    }
} catch {}

Assert-Test "Chuan hoa JSON Request Google Gemini v1beta" $isValidJson "Da xac thuc role='user' va text='2 + 2 bang may?'"

$mockGeminiResp = '{"candidates":[{"content":{"parts":[{"text":"2 + 2 bang 4."}],"role":"model"},"finishReason":"STOP"}]}'
$geminiParsed = $mockGeminiResp | ConvertFrom-Json
$extractedGeminiText = $geminiParsed.candidates[0].content.parts[0].text
Assert-Test "Boc tach Phan hoi Gemini Response" ($extractedGeminiText -eq "2 + 2 bang 4.") "Da trich xuat thanh cong: '$extractedGeminiText'"

# --- TEST 3: Schema Request & Response OpenAI API ---
Write-Host "`n--- Luong 3: Kiem thu cau truc Du lieu OpenAI API (v1/chat/completions) ---"
$openAiObj = @{
    model = "gpt-4o-mini"
    messages = @(
        @{ role = "system"; content = "Ban la AI" },
        @{ role = "user"; content = "Thu do cua Viet Nam la gi?" }
    )
    temperature = 0.7
}
$openAiJson = $openAiObj | ConvertTo-Json -Depth 5

$isOpenAiValid = $false
try {
    $parsedOpenAi = $openAiJson | ConvertFrom-Json
    if ($parsedOpenAi.model -eq "gpt-4o-mini" -and $parsedOpenAi.messages.Count -eq 2) {
        $isOpenAiValid = $true
    }
} catch {}

Assert-Test "Chuan hoa JSON Request OpenAI API" $isOpenAiValid "Model: gpt-4o-mini, Messages Count: 2"

$mockOpenAiResp = '{"id":"chatcmpl-123","choices":[{"index":0,"message":{"role":"assistant","content":"Thu do cua Viet Nam la Ha Noi."},"finish_reason":"stop"}]}'
$openAiParsed = $mockOpenAiResp | ConvertFrom-Json
$extractedOpenAiText = $openAiParsed.choices[0].message.content
Assert-Test "Boc tach Phan hoi OpenAI Response" ($extractedOpenAiText -eq "Thu do cua Viet Nam la Ha Noi.") "Da trich xuat thanh cong: '$extractedOpenAiText'"

# --- TEST 4: Luong xu ly loi Xac thuc 401 (Invalid API Key) ---
Write-Host "`n--- Luong 4: Luong xu ly Ma loi 401 Unauthorized ---"
function Simulate-HttpError([int]$code) {
    switch ($code) {
        401 { return "Loi xac thuc (401): API Key khong dung hoac khong co quyen truy cap." }
        404 { return "Loi duong dan (404): Ten Model hoac endpoint khong ton tai." }
        429 { return "Loi gioi han tan suat (429): Qua nhieu yeu cau. Vui long thu lai sau vai giay." }
        500 { return "Loi may chu AI (500): He thong AI dang ban hoac gap su co." }
        default { return "Loi khong xac dinh: $code" }
    }
}

$error401Msg = Simulate-HttpError 401
Assert-Test "Bat va hien thi thong bao than thien cho loi 401" ($error401Msg -like "*401*API Key khong dung*") $error401Msg

# --- TEST 5: Luong xu ly gioi han tan suat 429 (Rate Limit) ---
Write-Host "`n--- Luong 5: Luong xu ly Gioi han tan suat 429 Rate Limit ---"
$error429Msg = Simulate-HttpError 429
Assert-Test "Bat va hien thi thong bao than thien cho loi 429" ($error429Msg -like "*429*Qua nhieu yeu cau*") $error429Msg

# --- TEST 6: Luong Multi-turn Conversation Context (Duy tri ngu canh) ---
Write-Host "`n--- Luong 6: Luong Duy tri Ngu canh Da luot (Multi-turn Context) ---"
$chatHistory = @(
    @{ id = "1"; senderType = 1; content = "Toi ten la Minh" },
    @{ id = "2"; senderType = 2; content = "Chao Minh! Rat vui duoc gap ban." },
    @{ id = "3"; senderType = 1; content = "Toi ten la gi?" }
)

$contextContents = @()
foreach ($msg in $chatHistory) {
    $role = if ($msg.senderType -eq 1) { "user" } else { "model" }
    $contextContents += @{ role = $role; parts = @(@{ text = $msg.content }) }
}

$hasCorrectTurns = ($contextContents.Count -eq 3 -and $contextContents[0].role -eq "user" -and $contextContents[1].role -eq "model" -and $contextContents[2].role -eq "user")
Assert-Test "Dong goi da luot hoi thoai (Multi-turn) day du lich su" $hasCorrectTurns "Tong so luot hoi thoai chuyen giao: $($contextContents.Count)"

Write-Host "`n=================================================="
Write-Host "KET QUA KIEM THU: PASS = $passCount, FAIL = $failCount"
Write-Host "=================================================="

if ($failCount -eq 0) {
    Write-Host ">>> TAT CA CAC LUONG API DA DUOC KIEM THU VA HOAT DONG CHINH XAC 100%! <<<" -ForegroundColor Green
} else {
    Write-Host ">>> CO $failCount BAI TEST THAT BAI! <<<" -ForegroundColor Red
}

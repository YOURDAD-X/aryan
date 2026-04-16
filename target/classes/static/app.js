const API_BASE = 'http://localhost:8081/api';
let currentUser = null;

function showTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.tabs button').forEach(el => el.classList.remove('active'));
    document.getElementById(`${tabName}-tab`).classList.add('active');
    event.target.classList.add('active');
    document.getElementById('auth-msg').innerText = '';
}

async function register() {
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const email = document.getElementById('reg-email').value;
    
    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, email })
        });
        if (!res.ok) throw new Error(await res.text());
        const data = await res.json();
        handleLoginSuccess(data);
    } catch (e) {
        document.getElementById('auth-msg').innerText = e.message;
    }
}

async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        if (!res.ok) throw new Error(await res.text());
        const data = await res.json();
        handleLoginSuccess(data);
    } catch (e) {
        document.getElementById('auth-msg').innerText = e.message;
    }
}

function handleLoginSuccess(user) {
    currentUser = user;
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('dashboard-container').classList.remove('hidden');
    document.getElementById('user-display').innerText = `Hello, ${user.username}`;
    document.getElementById('credit-badge').innerText = `Credits: ${user.credits}`;
}

function logout() {
    currentUser = null;
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('dashboard-container').classList.add('hidden');
    document.getElementById('auth-msg').innerText = 'Logged out securely.';
}

async function scoreResume() {
    const fileInput = document.getElementById('resume-file');
    const jd = document.getElementById('job-description').value;
    const resultBox = document.getElementById('score-result');
    const loader = document.getElementById('score-loader');
    
    if (!fileInput.files[0] || !jd) {
        alert('Please provide both a resume file and a job description.');
        return;
    }
    
    resultBox.classList.add('hidden');
    loader.classList.remove('hidden');

    try {
        // Step 1: Upload and parse resume
        const formData = new FormData();
        formData.append('file', fileInput.files[0]);
        formData.append('userId', currentUser.id);

        const uploadRes = await fetch(`${API_BASE}/resume/upload`, {
            method: 'POST',
            body: formData
        });
        if (!uploadRes.ok) throw new Error(await uploadRes.text());
        const resumeData = await uploadRes.json();

        // Update credits
        currentUser.credits--;
        document.getElementById('credit-badge').innerText = `Credits: ${currentUser.credits}`;

        // Step 2: Score ATS
        const scoreRes = await fetch(`${API_BASE}/ats/score`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ resumeId: resumeData.id, jobDescription: jd })
        });
        if (!scoreRes.ok) throw new Error(await scoreRes.text());
        const scoreData = await scoreRes.json();

        displayScore(scoreData);

    } catch (e) {
        alert("Error: " + e.message);
    } finally {
        loader.classList.add('hidden');
    }
}

function displayScore(data) {
    document.getElementById('score-percentage').innerText = data.scorePercentage.toFixed(1);
    document.getElementById('plagiarism-score').innerText = data.plagiarismScore.toFixed(1);
    document.getElementById('score-suggestions').innerText = data.suggestions;
    
    const matchedDiv = document.getElementById('matched-keywords');
    matchedDiv.innerHTML = '';
    (data.matchedKeywords ? data.matchedKeywords.split(',') : []).forEach(kw => {
        if(kw.trim()) matchedDiv.innerHTML += `<span>${kw.trim()}</span>`;
    });

    const missingDiv = document.getElementById('missing-keywords');
    missingDiv.innerHTML = '';
    (data.missingKeywords ? data.missingKeywords.split(',') : []).forEach(kw => {
        if(kw.trim()) missingDiv.innerHTML += `<span>${kw.trim()}</span>`;
    });

    document.getElementById('score-result').classList.remove('hidden');
}

async function buildResume() {
    const data = {
        fullName: document.getElementById('rb-name').value || 'John Doe',
        email: document.getElementById('rb-email').value || 'john@example.com',
        phone: document.getElementById('rb-phone').value || '+1 234 567 890',
        summary: document.getElementById('rb-summary').value,
        skills: document.getElementById('rb-skills').value.split(',').map(s=>s.trim()),
        experiences: [{
            company: 'Tech Corp', role: 'Software Engineer', duration: '2020 - Present',
            description: 'Developed scalable microservices.'
        }],
        educations: [{
            institution: 'University of Engineering', degree: 'B.Sc. Computer Science', year: '2020'
        }]
    };

    try {
        const res = await fetch(`${API_BASE}/builder/generate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error(await res.text());
        const html = await res.text();

        const printWindow = window.open('', '_blank');
        printWindow.document.write(html);
        printWindow.document.close();
        printWindow.focus();
        setTimeout(() => { printWindow.print(); }, 500);

    } catch (e) {
        alert("Error mapping builder request: " + e.message);
    }
}

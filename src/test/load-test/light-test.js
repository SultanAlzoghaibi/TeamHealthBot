import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '5s',
};

export default function () {
    // 5 fixed test users to simulate a single team's check-ins
    const testUsers = ['U10001', 'U10002', 'U10003', 'U10004', 'U10005'];
    // __VU is 1-based, so subtract 1 for zero-based array index
    const userId = testUsers[(__VU - 1) % testUsers.length];

    const payload = {
        token: 'TEST_SLACK_TOKEN',
        team_id: 'T0001',
        team_domain: 'example',
        channel_id: 'C2147483705',
        channel_name: 'test',
        user_id: userId,
        user_name: 'testuser',
        command: '/checkin',
        text: '10',
        response_url: 'https://hooks.slack.com/commands/1234/5678',
        trigger_id: '123456.7890.abcdef',
    };

    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
    };

    const encoded = Object.entries(payload)
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        .join('&');

    http.post('http://localhost:8001/api/slack/commands', encoded, { headers });
    sleep(10);

}
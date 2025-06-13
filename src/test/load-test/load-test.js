import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '5s', target: 50 },
        { duration: '5s', target: 100 },
        { duration: '5s', target: 200 },
        { duration: '5s', target: 400 },
        { duration: '5s', target: 600 },
        { duration: '5s', target: 800 },  // peak
        { duration: '5s', target: 400 },
        { duration: '5s', target: 100 },
        { duration: '5s', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<2000'],
    },
};

export default function () {
    const payload = {
        token: 'TEST_SLACK_TOKEN',
        team_id: 'T0001',
        team_domain: 'example',
        channel_id: 'C2147483705',
        channel_name: 'test',
        user_id: `U${Math.floor(Math.random() * 10000000)}`,
        user_name: 'john',
        command: '/myscores',
        text: "lol",
        response_url: 'https://hooks.slack.com/commands/1234/5678',
        trigger_id: '13345224609.738474920.8088930838d88f008e0',
    };

    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
    };

    const encoded = Object.entries(payload)
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        .join('&');

    http.post('http://a79982d90e24a434aaa37603e950c448-1822801868.us-east-1.elb.amazonaws.com/api/slack/commands', encoded, { headers });
}
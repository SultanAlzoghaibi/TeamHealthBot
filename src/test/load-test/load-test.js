import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10000,            // virtual users (increase for more load)
    duration: '3s',    // test duration
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

    http.post('http://ac4bd24a00aac47cc863458c5176ea9f-106100581.us-east-1.elb.amazonaws.com/api/slack/commands', encoded, { headers });
}
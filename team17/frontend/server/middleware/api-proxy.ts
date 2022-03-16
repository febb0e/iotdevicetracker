import type { IncomingMessage, ServerResponse } from 'http'
import { createProxyMiddleware } from 'http-proxy-middleware'
import config from '#config'

// Temporary dev proxy until @nuxtjs/proxy module is available.

const apiProxyMiddleware = createProxyMiddleware('/api-proxy/**', {
	target: config.API_URL as string,
	changeOrigin: true,
	pathRewrite: { '^/api-proxy/': '/' },
	logLevel: 'debug',
})

export default async (req: IncomingMessage, res: ServerResponse) => {
	// Workaround for h3 not awaiting next.
	await new Promise<void>((resolve, reject) => {
		const next = (err?: unknown) => {
			if (err) {
				reject(err)
			} else {
				resolve()
			}
		}

		// @ts-expect-error -- incompatible types express.Request and http.IncomingMessage. This still works though.
		apiProxyMiddleware(req, res, next)
	})
}

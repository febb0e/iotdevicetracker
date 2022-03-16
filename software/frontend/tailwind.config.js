/* eslint-env node */

const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
	mode: 'jit',
	content: {
		files: [
			'./components/**/*.vue',
			'./layouts/**/*.vue',
			'./pages/**/*.vue',
			'./app.vue',
			'./plugins/**/*.ts',
			'./nuxt.config.ts',
		],
	},
	theme: {
		extend: {
			fontFamily: {
				sans: ['Inter var', ...defaultTheme.fontFamily.sans],
			},
		},
	},
	plugins: [
		require('@tailwindcss/forms'),
		require('@tailwindcss/typography'),
		require('@tailwindcss/aspect-ratio'),
	],
}

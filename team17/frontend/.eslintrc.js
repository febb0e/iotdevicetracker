/* eslint-env node */

module.exports = {
	root: true,
	ignorePatterns: [
		'/node_modules/**/*',
		'/.nuxt/**/*',
		'/.output/**/*',
		'/.yarn/**/*',
		'/nuxt.d.ts',
	],
	env: {
		'shared-node-browser': true,
		'es2021': true,
	},
	extends: ['eslint:recommended', 'plugin:prettier/recommended', 'prettier'],
	overrides: [
		{
			files: ['*.ts'],
			extends: [
				'plugin:@typescript-eslint/recommended',
				'plugin:@typescript-eslint/recommended-requiring-type-checking',
				'prettier', // We need to extend prettier again so that conflicting ts rules get disabled.
			],
			plugins: ['@typescript-eslint'],
			parser: '@typescript-eslint/parser',
			parserOptions: {
				ecmaVersion: 'latest',
				sourceType: 'module',
				project: './tsconfig.json',
			},
			rules: {
				'@typescript-eslint/no-non-null-assertion': 'off',
				'@typescript-eslint/no-explicit-any': [
					'warn',
					{
						fixToUnknown: true,
						ignoreRestArgs: true,
					},
				],
			},
		},
	],
}

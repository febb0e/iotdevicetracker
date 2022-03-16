<template>
	<div class="mt-6">
		<!-- ACCOUNT INFORMATION -->
		<section aria-labelledby="account-info-heading">
			<form @submit.prevent="updateAccountData">
				<div class="shadow sm:rounded-md sm:overflow-hidden">
					<div class="bg-white py-6 px-4 sm:p-6">
						<div>
							<h2
								id="account-info-heading"
								class="text-lg leading-6 font-medium text-gray-900"
							>
								Account information
							</h2>
							<p class="mt-1 text-sm text-gray-500">
								Here you can change your account information, like username,
								email or password. Note that changing your e-mail address will
								require you to re-verify your account and changing your password
								will log you out from all other devices.
							</p>
						</div>

						<div class="mt-6">
							<label
								for="currentPassword"
								class="block text-sm font-medium text-gray-700"
							>
								Current Password
							</label>
							<div class="mt-1 relative">
								<input
									v-model="accountData.currentPassword"
									id="currentPassword"
									name="currentPassword"
									type="password"
									autocomplete="current-password"
									placeholder="********"
									required="true"
									:class="[
										'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
										hasValidationErrors('currentPassword')
											? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
											: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
									]"
									:aria-invalid="hasValidationErrors('currentPassword')"
									aria-describedby="currentPassword-error"
									@input="clearValidationError('currentPassword')"
								/>
								<div
									v-show="hasValidationErrors('currentPassword')"
									class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
								>
									<ExclamationCircleIcon
										class="h-5 w-5 text-red-500"
										aria-hidden="true"
									/>
								</div>
							</div>
							<p id="currentPassword-error" class="mt-2 text-sm">
								<span
									v-if="hasValidationErrors('currentPassword')"
									class="text-red-600"
									>{{ validationErrors.currentPassword.defaultMessage }}</span
								>
								<span v-else class="text-gray-700"
									>Please confirm your identity by entering your current
									password, in order to be able to change your account
									information.</span
								>
							</p>
						</div>

						<div class="mt-6 grid grid-cols-4 gap-6">
							<div class="col-span-4 sm:col-span-2">
								<label
									for="username"
									class="block text-sm font-medium text-gray-700"
								>
									Username
								</label>
								<div class="mt-1 relative">
									<input
										v-model="accountData.username"
										id="username"
										name="username"
										autocomplete="username"
										placeholder="Jane Doe"
										required="true"
										:class="[
											'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
											hasValidationErrors('username')
												? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
												: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
										]"
										:aria-invalid="hasValidationErrors('username')"
										aria-describedby="username-error"
										@input="clearValidationError('username')"
									/>
									<div
										v-show="hasValidationErrors('username')"
										class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
									>
										<ExclamationCircleIcon
											class="h-5 w-5 text-red-500"
											aria-hidden="true"
										/>
									</div>
								</div>
								<p
									v-if="hasValidationErrors('username')"
									id="username-error"
									class="mt-2 text-sm text-red-600"
								>
									{{ validationErrors.username.defaultMessage }}
								</p>
							</div>

							<div class="col-span-4 sm:col-span-2">
								<label
									for="email"
									class="block text-sm font-medium text-gray-700"
								>
									Email address
								</label>
								<div class="mt-1 relative">
									<input
										v-model="accountData.email"
										id="email"
										name="email"
										type="email"
										autocomplete="email"
										placeholder="jane.doe@email.net"
										required="true"
										:class="[
											'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
											hasValidationErrors('email')
												? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
												: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
										]"
										:aria-invalid="hasValidationErrors('email')"
										aria-describedby="email-error"
										@input="clearValidationError('email')"
									/>
									<div
										v-show="hasValidationErrors('email')"
										class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
									>
										<ExclamationCircleIcon
											class="h-5 w-5 text-red-500"
											aria-hidden="true"
										/>
									</div>
								</div>
								<p
									v-if="hasValidationErrors('email')"
									id="email-error"
									class="mt-2 text-sm text-red-600"
								>
									{{ validationErrors.email.defaultMessage }}
								</p>
							</div>

							<div class="col-span-4 sm:col-span-2">
								<label
									for="newPassword"
									class="block text-sm font-medium text-gray-700"
								>
									New Password
								</label>
								<div class="mt-1 relative">
									<input
										v-model="accountData.newPassword"
										id="newPassword"
										name="newPassword"
										type="password"
										autocomplete="new-password"
										placeholder="********"
										:class="[
											'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
											hasValidationErrors('newPassword')
												? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
												: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
										]"
										:aria-invalid="hasValidationErrors('newPassword')"
										aria-describedby="newPassword-error"
										@input="clearValidationError('newPassword')"
									/>
									<div
										v-show="hasValidationErrors('newPassword')"
										class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
									>
										<ExclamationCircleIcon
											class="h-5 w-5 text-red-500"
											aria-hidden="true"
										/>
									</div>
								</div>
								<p
									v-if="hasValidationErrors('newPassword')"
									if="newPassword-error"
									class="mt-2 text-sm text-red-600"
								>
									{{ validationErrors.newPassword.defaultMessage }}
								</p>
							</div>

							<div class="col-span-4 sm:col-span-2">
								<label
									for="newPasswordConfirmation"
									class="block text-sm font-medium text-gray-700"
								>
									Confirm new Password
								</label>
								<div class="mt-1 relative">
									<input
										v-model="accountData.newPasswordConfirmation"
										id="newPasswordConfirmation"
										name="newPasswordConfirmation"
										type="password"
										autocomplete="new-password"
										placeholder="********"
										:class="[
											'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
											hasValidationErrors('newPasswordConfirmation')
												? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
												: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
										]"
										:aria-invalid="
											hasValidationErrors('newPasswordConfirmation')
										"
										aria-describedby="newPasswordConfirmation-error"
										@input="clearValidationError('newPasswordConfirmation')"
									/>
									<div
										v-show="hasValidationErrors('newPasswordConfirmation')"
										class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
									>
										<ExclamationCircleIcon
											class="h-5 w-5 text-red-500"
											aria-hidden="true"
										/>
									</div>
								</div>
								<p
									v-if="hasValidationErrors('newPasswordConfirmation')"
									id="newPasswordConfirmation-error"
									class="mt-2 text-sm text-red-600"
								>
									{{ validationErrors.newPasswordConfirmation.defaultMessage }}
								</p>
							</div>
						</div>
					</div>
					<div class="px-4 py-3 bg-gray-50 sm:px-6">
						<button
							type="submit"
							class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm"
						>
							<span v-if="isUpdatingAccount">Updating ...</span>
							<span v-else>Update</span>
						</button>
					</div>
				</div>
			</form>
		</section>

		<!-- 2-FACTOR-AUTHENTICATION -->
		<section class="mt-6" aria-labelledby="2fa-heading">
			<div class="shadow sm:rounded-md sm:overflow-hidden">
				<div class="bg-white py-6 px-4 sm:p-6">
					<div>
						<h2
							id="2fa-heading"
							class="text-lg leading-6 font-medium text-gray-900"
						>
							2-Factor-Authentication
						</h2>
						<p class="mt-1 text-sm text-gray-500">
							2-Factor-Authentication (2FA) helps you to secure your account by
							requiring a Time-based One-time Password (TOTP) in addition to
							your username and password on every login.
						</p>
					</div>

					<!-- MFA ENABLED -->
					<template v-if="store.authenticatedUser.mfaEnabled">
						<div class="rounded-md bg-green-50 p-4 mt-6">
							<div class="flex">
								<div class="flex-shrink-0">
									<CheckCircleIcon
										class="h-5 w-5 text-green-400"
										aria-hidden="true"
									/>
								</div>
								<div class="ml-3">
									<h3 class="text-sm font-medium text-green-800">
										Your account is secured
									</h3>
									<div class="mt-2 text-sm text-green-700">
										<p>
											You have already enabled 2-Factor-Authentication. On every
											login, you will be asked to enter your TOTP.
										</p>
									</div>
								</div>
							</div>
						</div>

						<button
							type="button"
							class="mt-6 inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
							@click="disableTOTPModalOpen = true"
						>
							Disable 2FA
						</button>
					</template>

					<!-- MFA DISABLED -->
					<template v-else>
						<template v-if="currentStep === 0">
							<div class="rounded-md bg-yellow-50 p-4 mt-6">
								<div class="flex">
									<div class="flex-shrink-0">
										<ExclamationIcon
											class="h-5 w-5 text-yellow-400"
											aria-hidden="true"
										/>
									</div>
									<div class="ml-3">
										<h3 class="text-sm font-medium text-yellow-800">
											2FA is currently disabled
										</h3>
										<div class="mt-2 text-sm text-yellow-700">
											<p>
												Consider enabling 2FA to improve the security of your
												account.
											</p>
										</div>
									</div>
								</div>
							</div>

							<button
								type="button"
								class="mt-6 inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
								@click="currentStep = 1"
							>
								Start 2FA Setup
							</button>
						</template>

						<!-- STEPPER -->
						<template v-else>
							<nav aria-label="Progress" class="mt-6">
								<ol
									role="list"
									class="border border-gray-300 rounded-md divide-y divide-gray-300 md:flex md:divide-y-0"
								>
									<li
										v-for="(step, stepIdx) in steps"
										:key="step.step"
										class="relative md:flex-1 md:flex"
									>
										<a
											v-if="currentStep > step.step"
											class="group flex items-center w-full"
										>
											<span
												class="px-6 py-4 flex items-center text-sm font-medium"
											>
												<span
													class="flex-shrink-0 w-10 h-10 flex items-center justify-center bg-indigo-600 rounded-full group-hover:bg-indigo-800"
												>
													<CheckIcon
														class="w-6 h-6 text-white"
														aria-hidden="true"
													/>
												</span>
												<span class="ml-4 text-sm font-medium text-gray-900">{{
													step.name
												}}</span>
											</span>
										</a>
										<a
											v-else-if="currentStep === step.step"
											class="px-6 py-4 flex items-center text-sm font-medium"
											aria-current="step"
										>
											<span
												class="flex-shrink-0 w-10 h-10 flex items-center justify-center border-2 border-indigo-600 rounded-full"
											>
												<span class="text-indigo-600">{{ step.step }}</span>
											</span>
											<span class="ml-4 text-sm font-medium text-indigo-600">{{
												step.name
											}}</span>
										</a>
										<a v-else class="group flex items-center">
											<span
												class="px-6 py-4 flex items-center text-sm font-medium"
											>
												<span
													class="flex-shrink-0 w-10 h-10 flex items-center justify-center border-2 border-gray-300 rounded-full group-hover:border-gray-400"
												>
													<span
														class="text-gray-500 group-hover:text-gray-900"
														>{{ step.step }}</span
													>
												</span>
												<span
													class="ml-4 text-sm font-medium text-gray-500 group-hover:text-gray-900"
													>{{ step.name }}</span
												>
											</span>
										</a>
										<template v-if="stepIdx !== steps.length - 1">
											<!-- Arrow separator for lg screens and up -->
											<div
												class="hidden md:block absolute top-0 right-0 h-full w-5"
												aria-hidden="true"
											>
												<svg
													class="h-full w-full text-gray-300"
													viewBox="0 0 22 80"
													fill="none"
													preserveAspectRatio="none"
												>
													<path
														d="M0 -2L20 40L0 82"
														vector-effect="non-scaling-stroke"
														stroke="currentcolor"
														stroke-linejoin="round"
													/>
												</svg>
											</div>
										</template>
									</li>
								</ol>
							</nav>

							<div>
								<!-- STEP 1: Confirm Identity (Setup) -->
								<div v-if="currentStep === 1">
									<form class="mt-4" @submit.prevent="setup">
										<p class="text-sm text-gray-700">
											Please confirm your identity by entering your current
											password in order to setup 2-Factor-Authentication.
											Setting up 2FA will log you out on all other devices.
										</p>
										<div class="mt-4">
											<label
												for="password"
												class="block text-sm font-medium text-gray-700"
											>
												Password
											</label>
											<div class="mt-1 relative">
												<input
													v-model="password"
													id="password"
													name="password"
													type="password"
													autocomplete="current-password"
													placeholder="********"
													required="true"
													:class="[
														'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
														invalidPassword
															? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
															: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
													]"
													:aria-invalid="invalidPassword"
													aria-describedby="password-error"
													@input="invalidPassword = false"
												/>
												<div
													v-show="invalidPassword"
													class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
												>
													<ExclamationCircleIcon
														class="h-5 w-5 text-red-500"
														aria-hidden="true"
													/>
												</div>
											</div>
											<p
												v-if="invalidPassword"
												if="password-error"
												class="mt-2 text-sm text-red-600"
											>
												Invalid password
											</p>
										</div>
										<div class="mt-5">
											<button
												type="submit"
												class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm"
											>
												<span v-if="isSettingUp">Confirming ...</span>
												<span v-else>Confirm Identity</span>
											</button>

											<button
												type="button"
												class="w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm mt-1 md:mt-0 sm:ml-2"
												@click="currentStep = 0"
											>
												Cancel Setup
											</button>
										</div>
									</form>
								</div>

								<!-- STEP 2: Setup TOTP App (Verify) -->
								<div v-else-if="currentStep === 2">
									<form class="mt-4" @submit.prevent="verify">
										<p class="text-sm text-gray-700">
											You successfully confirmed your identity. Please proceed
											by scanning the QR-Code with your favorite TOTP app. This
											can be Authy, Google Authenticator or your favourite
											password manager, for example. Afterwards, enter the
											current TOTP and click Verify.
										</p>
										<img :src="credentials.qrCode" />
										<p class="text-sm text-gray-700">
											Or register manually:<br />
											<b>{{ credentials.secret }}</b>
										</p>
										<div class="mt-4">
											<label
												for="totp"
												class="block text-sm font-medium text-gray-700"
											>
												TOTP
											</label>
											<div class="mt-1 relative">
												<input
													v-model="totp"
													id="totp"
													name="totp"
													type="text"
													autocomplete="one-time-code"
													placeholder="123456"
													required="true"
													:class="[
														'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
														totpInvalid
															? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
															: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
													]"
													:aria-invalid="totpInvalid"
													aria-describedby="totp-error"
													@input="totpInvalid = false"
												/>
												<div
													v-show="totpInvalid"
													class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
												>
													<ExclamationCircleIcon
														class="h-5 w-5 text-red-500"
														aria-hidden="true"
													/>
												</div>
											</div>
											<p
												v-if="totpInvalid"
												if="password-error"
												class="mt-2 text-sm text-red-600"
											>
												Invalid TOTP
											</p>
										</div>
										<div class="mt-5">
											<button
												type="submit"
												class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm"
											>
												<span v-if="isVerifying">Verifying ...</span>
												<span v-else>Verify</span>
											</button>

											<button
												type="button"
												class="w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm mt-1 md:mt-0 sm:ml-2"
												@click="currentStep = 0"
											>
												Cancel Setup
											</button>
										</div>
									</form>
								</div>

								<!-- STEP 3: RECOVERY CODES -->
								<div v-else>
									<div class="rounded-md bg-green-50 p-4 mt-6">
										<div class="flex">
											<div class="flex-shrink-0">
												<CheckCircleIcon
													class="h-5 w-5 text-green-400"
													aria-hidden="true"
												/>
											</div>
											<div class="ml-3">
												<h3 class="text-sm font-medium text-green-800">
													Your account is now secured
												</h3>
												<div class="mt-2 text-sm text-green-700">
													<p>
														You have successfully enabled
														2-Factor-Authentication. On every login, you will
														now be asked to enter your TOTP.
													</p>
												</div>
											</div>
										</div>
									</div>
									<p class="mt-2">
										Below, you can find a list of Recovery codes. They can be
										used in case you loose access to your Authenticator. Each
										code is only usable once. Please keep them at a safe place.
									</p>
									<div class="mt-2 columns-1 md:columns-2 lg:columns-4">
										<p v-for="(code, index) in recoveryCodes" :key="index">
											{{ code }}
										</p>
									</div>
									<button
										type="button"
										class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:w-auto sm:text-sm mt-4"
										@click="done"
									>
										Done
									</button>
								</div>
							</div>
						</template>
					</template>
				</div>
			</div>
		</section>

		<!-- DELETE ACCOUNT -->
		<section class="mt-6" aria-labelledby="2fa-heading">
			<div class="shadow sm:rounded-md sm:overflow-hidden">
				<div class="bg-white py-6 px-4 sm:p-6">
					<div>
						<h2
							id="2fa-heading"
							class="text-lg leading-6 font-medium text-gray-900"
						>
							Delete Account
						</h2>
						<p class="mt-1 text-sm text-gray-500">
							Here you can permanently delete your account, along with all
							associated data. This action cannot be undone.
						</p>
						<form class="mt-4" @submit.prevent="deleteAccount">
							<div class="mt-4">
								<label
									for="password"
									class="block text-sm font-medium text-gray-700"
								>
									Password
								</label>
								<div class="mt-1 relative">
									<input
										v-model="password"
										id="password"
										name="password"
										type="password"
										autocomplete="current-password"
										placeholder="********"
										required="true"
										:class="[
											'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
											invalidPassword
												? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
												: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
										]"
										:aria-invalid="invalidPassword"
										aria-describedby="password-error"
										@input="invalidPassword = false"
									/>
									<div
										v-show="invalidPassword"
										class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
									>
										<ExclamationCircleIcon
											class="h-5 w-5 text-red-500"
											aria-hidden="true"
										/>
									</div>
								</div>
								<p id="password-error" class="mt-2 text-sm">
									<span v-if="invalidPassword" class="text-red-600"
										>Invalid password</span
									>
									<span v-else class="text-gray-700"
										>Please confirm your identity by entering your current
										password in order to be able to delete your account.</span
									>
								</p>
							</div>
							<div class="mt-5">
								<button
									type="submit"
									class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:w-auto sm:text-sm"
								>
									<span v-if="isDeletingAccount">Deleting Account ...</span>
									<span v-else>Delete Account</span>
								</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</section>

		<DisableTotpModal
			:open="disableTOTPModalOpen"
			@disabled="onDisabled"
			@close="disableTOTPModalOpen = false"
		></DisableTotpModal>
	</div>
</template>

<script setup lang="ts">
	import {
		CheckCircleIcon,
		ExclamationIcon,
		CheckIcon,
	} from '@heroicons/vue/solid/index.js'
	import { ExclamationCircleIcon } from '@heroicons/vue/solid/index.js'
	import { useStore } from '~/stores/main'
	import { $api } from '~/composables/useApi'
	import { NotificationType, useNotificationStore } from '~/stores/notification'

	// SHARED
	const store = useStore()
	const notification = useNotificationStore()
	const router = useRouter()
	const password = ref('')
	const invalidPassword = ref(false)

	// ACCOUNT DATA
	const {
		validationErrors,
		hasValidationErrors,
		clearValidationError,
		fillValidationErrors,
	} = useValidationErrors()
	const accountData = reactive({
		currentPassword: '',
		username: store.authenticatedUser.username,
		email: store.authenticatedUser.email,
		newPassword: '',
		newPasswordConfirmation: '',
	})
	const isUpdatingAccount = ref(false)

	async function updateAccountData() {
		if (isUpdatingAccount.value) return
		isUpdatingAccount.value = true

		try {
			store.authenticatedUser = await $api<User>(`/auth/me`, {
				method: 'PATCH',
				body: {
					currentPassword: accountData.currentPassword,
					username:
						accountData.username.length > 0 ? accountData.username : null,
					email: accountData.email.length > 0 ? accountData.email : null,
					newPassword:
						accountData.newPassword.length > 0 ? accountData.newPassword : null,
					newPasswordConfirmation:
						accountData.newPassword.length > 0
							? accountData.newPasswordConfirmation
							: null,
				},
			})

			// Clear password inputs again
			accountData.currentPassword = ''
			accountData.newPassword = ''
			accountData.newPasswordConfirmation = ''

			// User might got unverified, when changing email.
			if (store.authenticatedUser.verified) {
				notification.show(
					NotificationType.SUCCESS,
					'Account updated',
					'Your account information have been updated successfully',
					10
				)
			} else {
				notification.show(
					NotificationType.SUCCESS,
					'Account updated',
					'Your account information have been updated successfully. Please re-verify your e-mail address.',
					10
				)
				router.push('/auth/verify')
			}
		} catch (err) {
			if (isValidationError(err)) {
				fillValidationErrors(err)
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isUpdatingAccount.value = false
		}
	}

	// TOTP
	const disableTOTPModalOpen = ref(false)
	const currentStep = ref(0)
	const steps = [
		{ step: 1, name: 'Confirm Identity' },
		{ step: 2, name: 'Setup TOTP App' },
		{ step: 3, name: 'Recovery Codes' },
	]

	interface TotpCredentials {
		secret: string
		qrCode: string
	}

	const isSettingUp = ref(false)
	const credentials = ref<TotpCredentials | null>(null)

	async function setup() {
		if (isSettingUp.value) return
		isSettingUp.value = true

		try {
			credentials.value = await $api<TotpCredentials>(`/totp/setup`, {
				method: 'POST',
				body: {
					password: password.value,
				},
			})
			currentStep.value = 2
		} catch (err) {
			if (isValidationError(err)) {
				invalidPassword.value = true
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isSettingUp.value = false
		}
	}

	const totp = ref('')
	const totpInvalid = ref(false)
	const isVerifying = ref(false)
	const recoveryCodes = ref<string[]>([])

	async function verify() {
		if (isVerifying.value) return
		isVerifying.value = true

		try {
			recoveryCodes.value = await $api<string[]>(`/totp/verify`, {
				method: 'POST',
				body: {
					totp: totp.value,
				},
			})
			currentStep.value = 3
		} catch (err) {
			if (isValidationError(err)) {
				totpInvalid.value = true
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isVerifying.value = false
		}
	}

	function done() {
		password.value = ''
		totp.value = ''
		credentials.value = null
		recoveryCodes.value = []
		currentStep.value = 0
		store.authenticatedUser.mfaEnabled = true
	}

	function onDisabled() {
		notification.show(
			NotificationType.INFO,
			'2FA disabled',
			'2-Factor-Authentication has been disabled successfully',
			10
		)
		store.authenticatedUser.mfaEnabled = false
	}

	// DELETE ACCOUNT
	const isDeletingAccount = ref(false)

	async function deleteAccount() {
		if (
			isDeletingAccount.value ||
			!window.confirm(
				'Do you really want to delete your account? This action cannot be undone!'
			)
		) {
			return
		}

		isDeletingAccount.value = true
		try {
			await $api(`/auth/me`, {
				method: 'DELETE',
				body: {
					password: password.value,
				},
			})
			notification.show(
				NotificationType.SUCCESS,
				'Account deleted',
				'Your account has been permanently deleted',
				10
			)
			router.push('/auth/login')
		} catch (err) {
			if (isValidationError(err)) {
				invalidPassword.value = true
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isDeletingAccount.value = false
		}
	}
</script>

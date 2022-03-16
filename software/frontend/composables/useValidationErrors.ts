import { FetchError } from 'ohmyfetch'
import { isValidationError } from './useApi'

interface ValidationError {
	code: string
	field: string
	defaultMessage: string
}

type ValidationErrors = Record<string, ValidationError>

export const useValidationErrors = () => {
	const validationErrors = reactive({} as ValidationErrors)

	function hasValidationErrors(field?: string) {
		if (field === undefined) {
			return Object.keys(validationErrors).length > 0
		}
		return validationErrors[field] !== undefined
	}

	function fillValidationErrors(err: FetchError) {
		if (!isValidationError(err)) {
			throw new Error('Not a validation error.')
		}

		const errors = err.response._data as ValidationError[]
		errors.forEach((e) => (validationErrors[e.field] = e))
	}

	function clearValidationError(field: string) {
		delete validationErrors[field]
	}

	return {
		validationErrors,
		hasValidationErrors,
		clearValidationError,
		fillValidationErrors,
	}
}

# TransactionDecoder

Simple library for decoding smart contract call inputs.

## Build
`gradle build`

## Methods
`getFunctionName(String abi, String transaction)` - returns the name of the function called in the contract, given the ABI and transaction JSONs.
`getContractFunctionNamesByHash(String abi)` - returns the map with method hashes as keys and method names as values.

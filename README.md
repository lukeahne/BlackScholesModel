# Black Scholes Option Pricing Model & Greeks

A Java implementation of the Black Scholes model for pricing European call and put options and calculating the primary option Greeks.

## Overview

This project implements the Black Scholes pricing model from scratch in Java. The model calculates theoretical option prices using the underlying asset price, strike price, time to expiration, risk-free rate, and volatility.

It also calculates **Delta, Gamma, Vega, Theta, and Rho**, implements standard normal PDF/CDF functions, and includes put-call parity verification and an interactive command-line interface.

## Running the Program

Compile:

```bash
javac BlackScholesModel.java
```

Run:

```bash
java BlackScholesModel
```

## Features

* European call and put option pricing
* Black-Scholes `d1` and `d2` calculations
* Delta, Gamma, Vega, Theta, and Rho
* Standard normal PDF and CDF implementation
* Put-call parity verification
* Input validation
* Predefined pricing examples
* Interactive user input for custom scenarios

## Model Inputs

| Input | Description             |
| ----- | ----------------------- |
| `S`   | Underlying asset price  |
| `K`   | Strike price            |
| `T`   | Time to expiration      |
| `r`   | Risk-free interest rate |
| `σ`   | Annualized volatility   |

## Example

The program includes predefined examples for:

* At-the-money European call and put options
* In-the-money European call options
* Interactive pricing using user-specified inputs

Example parameters:

```text
S = $100
K = $100
T = 1 year
r = 5%
σ = 20%
```

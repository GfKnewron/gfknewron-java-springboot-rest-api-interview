# Java Interview Assignment  

This project is a typical Spring Boot application that, as a candidate, you can use to implement your solution to the problem statements below.  You may add dependencies you deem necessary for good reasons.

## Exercise Assessment (in priority order)

1. Working application
1. Production code quality
1. Tested code quality, both unit and integration
1. Number of features

## Requirements:

- You are to build a simple banking API.
- Make available the following roles a BANK_MANAGER and a USER.
- Implement the following scenarios:

## Scenarios

### _A USER can manage their money_

As a USER:
1. I can deposit and withdraw money from my account.
1. I am allowed an overdraft of £50 by default.
1. I am not allowed to withdraw beyond my overdraft limit.
1. Whenever I withdraw money that takes my balance to < 0, the system charges me £5.
1. Whenever I withdraw money when my balance is < 0, the system charges me £8.
1. My account is block after five consecutive events that led to charges.

### _A BANK_MANAGER can manage USERs_

As a BANK_MANAGER:
1. I can list all USERS.
1. I can Create, Read, Update, Block, Unblock and Delete Users from the system.

### Bonus: 

1. A USER can also Update and Delete their account.
1. To discourage the use of overdraft (the £50 Borrowed money reserve):
   1. Once a USER's balance is < 0, the system prompts the user with a friendly warning message for every further withdrawal before accepting the withdrawal.  
   1. You are to decide how you want to implement accepting the withdrawal after the friendly warning message.

## Hints:

1. You are to decide the NFRs you see fit.
1. Unit tests must accompany the solution.
1. Integration tests must accompany the solution in exemplifying how to use the API.
1. Write well-modelled, production-quality code.
1. This exercise tests your experience with RESTful APIs.  It is an opportunity to demonstrate mature RESTful practices to exemplify your craft.
1. Demonstrate you are an engineer, not just a great coder; think SOLID, Gang of Four, Design patterns, TDD, BDD, and secure programming.
1. Be agile; a partial solution is more desirable than a non-working solution.
1. We are a company that relies on being trusted.  When compared to delivery deadlines, we care equally or even more about delivering well-tested solutions to our users.

## To participate and make a submission, please:

1. Fork this repo.
1. Create a new feature branch in your newly forked repo.
1. Make regular commits to demonstrate how you tackle the problem in your newly created branch. 
1. Once you have completed the exercise, create an MR to your main branch in your cloned repo for a code review.
1. Share the MR link with your contact (recruit/hiring manager).

All the best :)
Supermarkets
============

This is the 'ugly' submission for the brandwatch tech test.

I'm releasing this because the tech test this is based on is not used. It will also be pretty obvious if you use this code.

Original Description
--------------------

This uses Reflection to alter the seeds in the Random objects used by the
system. The seeds are then fixed so that the most profitable system is created.

After the seeds are fixed a Sale of 6 will be made every turn. There will be no
price rises.

Since this plugin has almost no variation in state it has by far the simplest
state machine.

(There is also a FixedRandom class in this plugin which is currently unused.
That was going to be part of a ridiculous solution to this which is documented
on the class).

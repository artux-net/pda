package net.artux.pda.map.model;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

public class MyStateMachine<E, S extends State<E>> implements StateMachine<E, S> {

    /** The entity that owns this state machine. */
    protected E owner;

    /** The current state the owner is in. */
    protected S currentState;

    /** The last state the owner was in. */
    protected S previousState;

    /** The global state of the owner. Its logic is called every time the FSM is updated. */
    protected S globalState;

    /** Creates a {@code DefaultStateMachine} with no owner, initial state and global state. */
    public MyStateMachine () {
        this(null, null, null);
    }

    /** Creates a {@code DefaultStateMachine} for the specified owner.
     * @param owner the owner of the state machine */
    public MyStateMachine (E owner) {
        this(owner, null, null);
    }

    /** Creates a {@code DefaultStateMachine} for the specified owner and initial state.
     * @param owner the owner of the state machine
     * @param initialState the initial state */
    public MyStateMachine (E owner, S initialState) {
        this(owner, initialState, null);
    }

    /** Creates a {@code DefaultStateMachine} for the specified owner, initial state and global state.
     * @param owner the owner of the state machine
     * @param initialState the initial state
     * @param globalState the global state */
    public MyStateMachine (E owner, S initialState, S globalState) {
        this.owner = owner;
        this.setInitialState(initialState);
        changeGlobalState(globalState, false);
    }
    /** Returns the owner of this state machine. */
    public E getOwner () {
        return owner;
    }

    /** Sets the owner of this state machine.
     * @param owner the owner. */
    public void setOwner (E owner) {
        this.owner = owner;
    }

    @Override
    public void setInitialState (S state) {
        this.previousState = null;
        this.currentState = state;
    }

    public void changeGlobalState (S state, boolean enter) {

        if (this.globalState!=null)
            globalState.exit(owner);
        this.globalState = state;
        if (this.globalState!=null && enter)
            globalState.enter(owner);
    }

    @Override
    public void setGlobalState (S state) {
        changeGlobalState(state, false);
    }

    @Override
    public S getCurrentState () {
        return currentState;
    }

    @Override
    public S getGlobalState () {
        return globalState;
    }

    @Override
    public S getPreviousState () {
        return previousState;
    }

    /** Updates the state machine by invoking first the {@code execute} method of the global state (if any) then the {@code execute}
     * method of the current state. */
    @Override
    public void update () {
        // Execute the global state (if any)
        if (globalState != null) globalState.update(owner);

        // Execute the current state (if any)
        if (currentState != null) currentState.update(owner);
    }

    @Override
    public void changeState (S newState) {
        // Keep a record of the previous state
        previousState = currentState;

        // Call the exit method of the existing state
        if (currentState != null) currentState.exit(owner);

        // Change state to the new state
        currentState = newState;

        // Call the entry method of the new state
        if (currentState != null) currentState.enter(owner);
    }

    @Override
    public boolean revertToPreviousState () {
        if (previousState == null) {
            return false;
        }

        changeState(previousState);
        return true;
    }

    /** Indicates whether the state machine is in the given state.
     * <p>
     * This implementation assumes states are singletons (typically an enum) so they are compared with the {@code ==} operator
     * instead of the {@code equals} method.
     *
     * @param state the state to be compared with the current state
     * @return true if the current state and the given state are the same object. */
    @Override
    public boolean isInState (S state) {
        return currentState == state;
    }

    /** Handles received telegrams. The telegram is first routed to the current state. If the current state does not deal with the
     * message, it's routed to the global state's message handler.
     *
     * @param telegram the received telegram
     * @return true if telegram has been successfully handled; false otherwise. */
    @Override
    public boolean handleMessage (Telegram telegram) {

        // First see if the current state is valid and that it can handle the message
        if (currentState != null && currentState.onMessage(owner, telegram)) {
            return true;
        }

        // If not, and if a global state has been implemented, send
        // the message to the global state
        if (globalState != null && globalState.onMessage(owner, telegram)) {
            return true;
        }

        return false;
    }
}
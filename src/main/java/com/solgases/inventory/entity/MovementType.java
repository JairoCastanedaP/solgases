package com.solgases.inventory.entity;

/**
 * The kind of inventory movement. The {@link MovementDirection} it implies is fixed for
 * {@link #ENTRY} and {@link #EXIT}; only {@link #ADJUSTMENT} takes an explicit direction.
 */
public enum MovementType {
    ENTRY,
    EXIT,
    ADJUSTMENT
}

package com.example.raveh.rscontrol;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Raveh on 27/10/2014.
 */
public class BleSender
{
    public static class Command {
        public BluetoothGattCharacteristic characteristic;
        public BluetoothGatt bluetoothGatt;

        public void execute()
        {
            bluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    public static class Descriptor {
        public BluetoothGattDescriptor descriptor;
        public BluetoothGatt bluetoothGatt;
        public void execute()
        {
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    private LinkedList<Descriptor> m_DescList;
    private LinkedList<Command> m_CmdList;
    private boolean m_DescWaiting;
    private boolean m_CmdWaiting;

    public BleSender()
    {
        m_DescList = new LinkedList<Descriptor>();
        m_CmdList = new LinkedList<Command>();
    }

    public void addCommand(Command command)
    {
        synchronized (m_CmdList) {
            m_CmdList.add(command);
        }
        update();
    }

    public void addDescriptor(Descriptor descriptor)
    {
        synchronized (m_DescList) {
            m_DescList.add(descriptor);
        }
        update();
    }

    public void releaseCommand()
    {
        synchronized (m_CmdList) {
            m_CmdWaiting = false;
        }
        update();
    }

    public void releaseDescriptor()
    {
        synchronized (m_DescList) {
            m_DescWaiting = false;
        }
        update();
    }

    private void update()
    {
        synchronized (m_DescList) {
            synchronized (m_CmdList) {
                if (!m_DescWaiting && !m_CmdWaiting && m_DescList.size() > 0)
                {
                    m_DescList.get(m_DescList.size() - 1).execute();
                    m_DescList.remove(m_DescList.size() - 1);
                    m_DescWaiting = true;
                }

                if (!m_DescWaiting && !m_CmdWaiting && m_CmdList.size() > 0)
                {
                    m_CmdList.get(m_CmdList.size() - 1).execute();
                    m_CmdList.remove(m_CmdList.size() - 1);
                    m_CmdWaiting = true;
                }
            }
        }
    }
}

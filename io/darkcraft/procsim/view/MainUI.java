package io.darkcraft.procsim.view;

import io.darkcraft.procsim.controller.MemoryType;
import io.darkcraft.procsim.controller.PipelineType;
import io.darkcraft.procsim.controller.RegisterType;
import io.darkcraft.procsim.controller.SimulatorType;
import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.memory.cache.AbstractCache;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class MainUI implements ActionListener
{
	public static MainUI	i;
	private JFrame			mainFrame;
	private JButton			instructionSelectButton;
	private JTextField		instructionSelectField;
	private JButton			memorySelectButton;
	private JTextField		memorySelectField;
	private JComboBox		memoryTypeBox;
	private JTextField		memorySizeField;
	private JTextField		cacheLineSizeField;
	private JButton			addMemoryButton;
	private JList			currentMemField;
	private IMemory			currentMemory	= null;
	private JComboBox		simulatorTypeBox;
	private JTextField		numPipelines;
	private JButton			runButton;
	private JButton			removeButton;
	private JComboBox		pipelineTypeBox;
	private JComboBox		registerTypeBox;

	public static void main(String... args)
	{
		i = new MainUI();
	}

	private static final File	f	= new File("config.cfg");

	private void save()
	{
		PrintWriter writer = null;
		try
		{
			if (!f.exists())
				f.createNewFile();
			writer = new PrintWriter(f);
			if (!instructionSelectField.getText().isEmpty())
				writer.println("ISF:" + instructionSelectField.getText());
			if (!memorySelectField.getText().isEmpty())
				writer.println("MSF:" + memorySelectField.getText());
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (writer != null)
				writer.close();
		}
	}

	private void load()
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				String[] split = line.split(":", 2);
				if (split[0].equals("ISF"))
					instructionSelectField.setText(split[1]);
				if (split[0].equals("MSF"))
					memorySelectField.setText(split[1]);
			}
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (reader != null)
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
				}
		}
	}

	public MainUI()
	{
		mainFrame = new JFrame();
		mainFrame.setLayout(GridBagHelper.getLayout());
		mainFrame.setTitle("ProcSim");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		registerLabels();
		registerText();
		registerButtons();
		registerCombos();
		mainFrame.setSize(800, 600);
		mainFrame.setVisible(true);
		mainFrame.pack();
		mainFrame.setMinimumSize(new Dimension(mainFrame.getWidth(), mainFrame.getHeight()));
		load();
	}

	private void registerCombos()
	{
		memoryTypeBox = new JComboBox();
		memoryTypeBox.setBackground(Color.WHITE);
		for (MemoryType type : MemoryType.values())
			memoryTypeBox.addItem(type);
		mainFrame.add(memoryTypeBox, GridBagHelper.getConstraints(0, 3, 2, 1));

		simulatorTypeBox = new JComboBox();
		simulatorTypeBox.setBackground(Color.WHITE);
		simulatorTypeBox.addActionListener(this);
		for (SimulatorType s : SimulatorType.values())
			simulatorTypeBox.addItem(s);
		mainFrame.add(simulatorTypeBox, GridBagHelper.getConstraints(2, 3, 2, 1));

		pipelineTypeBox = new JComboBox();
		pipelineTypeBox.setBackground(Color.WHITE);
		for (PipelineType s : PipelineType.values())
			pipelineTypeBox.addItem(s);
		mainFrame.add(pipelineTypeBox, GridBagHelper.getConstraints(4, 3, 2, 1));

		registerTypeBox = new JComboBox();
		registerTypeBox.setBackground(Color.WHITE);
		for (RegisterType s : RegisterType.values())
			registerTypeBox.addItem(s);
		mainFrame.add(registerTypeBox, GridBagHelper.getConstraints(6, 3, 2, 1));
	}

	private void registerButtons()
	{
		instructionSelectButton = new JButton("Browse");
		instructionSelectButton.addActionListener(this);
		instructionSelectButton.setPreferredSize(new Dimension(100, 20));
		mainFrame.add(instructionSelectButton, GridBagHelper.getConstraints(9, 0));

		memorySelectButton = new JButton("Browse");
		memorySelectButton.addActionListener(this);
		memorySelectButton.setPreferredSize(new Dimension(100, 20));
		mainFrame.add(memorySelectButton, GridBagHelper.getConstraints(9, 1));

		addMemoryButton = new JButton("Add");
		addMemoryButton.addActionListener(this);
		mainFrame.add(addMemoryButton, GridBagHelper.getConstraints(0, 6, 2, 1));

		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		mainFrame.add(removeButton, GridBagHelper.getConstraints(0, 11, 2, 1));

		runButton = new JButton("Run");
		runButton.addActionListener(this);
		mainFrame.add(runButton, GridBagHelper.getConstraints(2, 6, 2, 1));
	}

	private void registerText()
	{
		instructionSelectField = new JTextField();
		instructionSelectField.setPreferredSize(new Dimension(580, 20));
		mainFrame.add(instructionSelectField, GridBagHelper.getConstraints(3, 0, 6, 1));

		memorySelectField = new JTextField();
		memorySelectField.setPreferredSize(new Dimension(580, 20));
		mainFrame.add(memorySelectField, GridBagHelper.getConstraints(3, 1, 6, 1));

		memorySizeField = new JTextField("1024");
		mainFrame.add(memorySizeField, GridBagHelper.getConstraints(1, 4, 1, 1));

		cacheLineSizeField = new JTextField("4");
		mainFrame.add(cacheLineSizeField, GridBagHelper.getConstraints(1, 5, 1, 1));

		currentMemField = new JList();
		currentMemField.setPreferredSize(new Dimension(200, 100));
		currentMemField.setBackground(Color.WHITE);
		currentMemField.setLayoutOrientation(JList.VERTICAL);
		mainFrame.add(currentMemField, GridBagHelper.getConstraints(0, 7, 2, 4));

		numPipelines = new JTextField("1");
		mainFrame.add(numPipelines, GridBagHelper.getConstraints(3, 4, 1, 1));
	}

	private void registerLabels()
	{
		JLabel fileSelectLabel = new JLabel("Instructions File:");
		mainFrame.add(fileSelectLabel, GridBagHelper.getConstraints(0, 0, 3, 1));

		JLabel memorySelectLabel = new JLabel("Memory File:");
		mainFrame.add(memorySelectLabel, GridBagHelper.getConstraints(0, 1, 3, 1));

		JLabel memoryTypeLabel = new JLabel("Memory Type");
		mainFrame.add(memoryTypeLabel, GridBagHelper.getConstraints(0, 2, 2, 1, GridBagConstraints.CENTER));

		JLabel memorySizeLabel = new JLabel("Size:");
		mainFrame.add(memorySizeLabel, GridBagHelper.getConstraints(0, 4, 1, 1));

		JLabel cacheLineSizeLabel = new JLabel("Cache Line Size:");
		mainFrame.add(cacheLineSizeLabel, GridBagHelper.getConstraints(0, 5, 1, 1));

		JLabel simulatorTypeLabel = new JLabel("Simulator Type");
		mainFrame.add(simulatorTypeLabel, GridBagHelper.getConstraints(2, 2, 2, 1, GridBagConstraints.CENTER));

		JLabel numPipelinesLabel = new JLabel("Num Pipelines:");
		mainFrame.add(numPipelinesLabel, GridBagHelper.getConstraints(2, 4, 1, 1));

		JLabel pipelineTypeLabel = new JLabel("Pipeline Type");
		mainFrame.add(pipelineTypeLabel, GridBagHelper.getConstraints(4, 2, 2, 1, GridBagConstraints.CENTER));

		JLabel registerTypeLabel = new JLabel("Registers Type");
		mainFrame.add(registerTypeLabel, GridBagHelper.getConstraints(6, 2, 2, 1, GridBagConstraints.CENTER));
	}

	private int getInt(JTextField field)
	{
		String s = field.getText();
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	private int validateNumPipelines()
	{
		SimulatorType s = (SimulatorType) simulatorTypeBox.getSelectedItem();
		int max = s.maxPipelines;
		int cur = getInt(numPipelines);
		if (cur > max)
			numPipelines.setText("" + max);
		else if (cur < 1)
			numPipelines.setText("" + 1);
		return getInt(numPipelines);
	}

	private void updateMemoryList()
	{
		currentMemField.removeAll();
		IMemory[] stack;
		if (currentMemory != null)
			stack = currentMemory.getStack();
		else
			stack = new IMemory[] {};
		currentMemField.setListData(stack);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == instructionSelectButton)
		{
			JFileChooser fileChooser = new JFileChooser();
			if (instructionSelectField.getText() != null)
				fileChooser.setCurrentDirectory(new File(instructionSelectField.getText()));
			int returnValue = fileChooser.showOpenDialog(instructionSelectButton);
			if (returnValue == JFileChooser.APPROVE_OPTION)
				instructionSelectField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			save();
		}
		if (source == memorySelectButton)
		{
			JFileChooser fileChooser = new JFileChooser();
			if (memorySelectField.getText() != null)
				fileChooser.setCurrentDirectory(new File(memorySelectField.getText()));
			int returnValue = fileChooser.showOpenDialog(memorySelectButton);
			if (returnValue == JFileChooser.APPROVE_OPTION)
				memorySelectField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			save();
		}
		if (source == addMemoryButton)
			addMemory();
		if (source == numPipelines || source == simulatorTypeBox || source == runButton)
			validateNumPipelines();
		if (source == runButton)
			run();
		if (source == removeButton)
			removeButtonPressed();
	}

	private void addMemory()
	{
		MemoryType type = (MemoryType) memoryTypeBox.getSelectedItem();
		if (type.requiresNextLevel == false)
			currentMemory = null;
		else if (currentMemory == null)
		{
			JOptionPane.showMessageDialog(mainFrame, "A cache cannot be created without Main Memory", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		currentMemory = type.getMemory(getInt(memorySizeField), getInt(cacheLineSizeField), currentMemory, new File(memorySelectField.getText()));
		updateMemoryList();
	}

	private void run()
	{
		if (currentMemory == null)
		{
			JOptionPane.showMessageDialog(mainFrame, "No memory has been specified", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		currentMemory = currentMemory.clone();
		RegisterType regType = (RegisterType) registerTypeBox.getSelectedItem();
		PipelineType pipType = (PipelineType) pipelineTypeBox.getSelectedItem();
		InstructionReader reader = new InstructionReader(new File(instructionSelectField.getText()));
		IRegisterBank registers = regType.construct();
		AbstractPipeline[] pipes = new AbstractPipeline[validateNumPipelines()];
		for (int i = 0; i < pipes.length; i++)
			pipes[i] = pipType.construct(currentMemory, registers, reader);
		AbstractSimulator s = ((SimulatorType) simulatorTypeBox.getSelectedItem()).getSimulator(currentMemory, registers, reader, pipes);
		new OutputUI(s);
	}

	private void removeButtonPressed()
	{
		IMemory toRemove = (IMemory) currentMemField.getSelectedValue();
		if (toRemove instanceof AbstractCache)
		{
			AbstractCache beingRemoved = (AbstractCache) toRemove;
			if (beingRemoved == currentMemory)
			{
				currentMemory = beingRemoved.nextLevel;
				if (currentMemory instanceof AbstractCache)
					((AbstractCache) currentMemory).setLevel(1);
			}
			else if (currentMemory instanceof AbstractCache)
			{
				currentMemory = ((AbstractCache) currentMemory).cloneUp(beingRemoved, beingRemoved.nextLevel.clone());
			}
		}
		else if (toRemove != null)
		{
			currentMemory = null;
			currentMemField.setListData(new Object[] {});
		}
		updateMemoryList();
	}
}
